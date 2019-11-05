import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class LogAnalyzer {
   //constants to be used when pulling data out of input
   //leave these here and refer to them to pull out values
   private static final String START_TAG = "START";
   private static final int START_NUM_FIELDS = 3;
   private static final int START_SESSION_ID = 1;
   private static final int START_CUSTOMER_ID = 2;
   private static final String BUY_TAG = "BUY";
   private static final int BUY_NUM_FIELDS = 5;
   private static final int BUY_SESSION_ID = 1;
   private static final int BUY_PRODUCT_ID = 2;
   private static final int BUY_PRICE = 3;
   private static final int BUY_QUANTITY = 4;
   private static final String VIEW_TAG = "VIEW";
   private static final int VIEW_NUM_FIELDS = 4;
   private static final int VIEW_SESSION_ID = 1;
   private static final int VIEW_PRODUCT_ID = 2;
   private static final int VIEW_PRICE = 3;
   private static final String END_TAG = "END";
   private static final int END_NUM_FIELDS = 2;
   private static final int END_SESSION_ID = 1;

   //a good example of what you will need to do next
   //creates a map of sessions to customer ids
   private static void processStartEntry(
           final String[] words,
           final Map<String, List<String>> sessionsFromCustomer) {
      if (words.length != START_NUM_FIELDS) {
         return;
      }

      //check if there already is a list entry in the map
      //for this customer, if not create one
      List<String> sessions = sessionsFromCustomer
              .get(words[START_CUSTOMER_ID]);
      if (sessions == null) {
         sessions = new LinkedList<>();
         sessionsFromCustomer.put(words[START_CUSTOMER_ID], sessions);
      }

      //now that we know there is a list, add the current session
      sessions.add(words[START_SESSION_ID]);
   }

   //similar to processStartEntry, should store relevant view
   //data in a map - model on processStartEntry, but store
   //your data to represent a view in the map (not a list of strings)
   private static void processViewEntry(final String[] words,
                                        final Map<String, List<View>> viewsFromSession) {
      if (words.length != VIEW_NUM_FIELDS) {
         return;
      }

      //check if there already is a list entry in the map
      //for this customer, if not create one
      List<View> views = viewsFromSession
              .get(words[VIEW_SESSION_ID]);
      if (views == null) {
         views = new LinkedList<View>();
         viewsFromSession.put(words[VIEW_SESSION_ID], views);
      }

      //now that we know there is a list, add the current product
      View v = new View(words[VIEW_SESSION_ID], words[VIEW_PRODUCT_ID], words[VIEW_PRICE]);
      views.add(v);
//      System.out.println(sessions);
   }

   //similar to processStartEntry, should store relevant purchases
   //data in a map - model on processStartEntry, but store
   //your data to represent a purchase in the map (not a list of strings)
   private static void processBuyEntry(
           final String[] words,
           final Map<String, List<Buy>> buysFromSession) {
      if (words.length != BUY_NUM_FIELDS) {
         return;
      }

      //check if there already is a list entry in the map
      //for this customer, if not create one
      List<Buy> buy = buysFromSession
              .get(words[BUY_SESSION_ID]);
      if (buy == null) {
         buy = new LinkedList<>();
         buysFromSession.put(words[BUY_SESSION_ID], buy);
      }

      Buy b = new Buy(words[BUY_SESSION_ID], words[BUY_PRODUCT_ID], words[BUY_PRICE], words[BUY_QUANTITY]);
      //now that we know there is a list, add the current session
      buy.add(b);
   }

   private static void processEndEntry(final String[] words) {
      if (words.length != END_NUM_FIELDS) {
         return;
      }
   }

   //this is called by processFile below - its main purpose is
   //to process the data using the methods you write above
   private static void processLine(
           final String line,
           final Map<String, List<String>> sessionsFromCustomer,
           final Map<String, List<View>> viewsFromSession,
           final Map<String, List<Buy>> buysFromSession
           /* add parameters as needed */
   ) {
      final String[] words = line.split("\\h");

      if (words.length == 0) {
         return;
      }

      switch (words[0]) {
         case START_TAG:
            processStartEntry(words, sessionsFromCustomer);
            break;
         case VIEW_TAG:
            processViewEntry(words, viewsFromSession);
            break;
         case BUY_TAG:
            processBuyEntry(words, buysFromSession);
            break;
         case END_TAG:
            processEndEntry(words);
            break;
      }
   }

   //write this after you have figured out how to store your data
   //make sure that you understand the problem
   private static void printSessionPriceDifference(final Map<String, List<Buy>> buysFromSession, final Map<String, List<View>> viewsFromSession) {
      System.out.println("Price Difference for Purchased Product by Session");

      // go through buy list and get price
      // go through view list and add sumprice and one to productsum
      // avg the price
      // find the difference

      for (Map.Entry<String, List<Buy>> entry : buysFromSession.entrySet()) {
         Double viewprice = 0.0;
         int productviewsum = 0;
         String key = entry.getKey();
         System.out.println(entry.getKey());
         int numofproducts = viewsFromSession.get(key).size();
         List<View> lis;
         lis = viewsFromSession.get(key);
         if (lis == null) {

         } else {
            for (int i = 0; i < lis.size(); i++) {
               viewprice += Double.valueOf(lis.get(i).getPrice());
               productviewsum += 1;
            }
            List<Buy> blis;
            blis = buysFromSession.get(key);
            for (int j = 0; j < blis.size(); j++)
               System.out.println("\t" + blis.get(j).getProductID() + " " + (Double.valueOf(blis.get(j).getPrice()) - (viewprice / numofproducts)));
         }
         System.out.println("");
      }
   }

   //write this after you have figured out how to store your data
   //make sure that you understand the problem
   private static void printCustomerItemViewsForPurchase(final Map<String, List<Buy>> buysFromSession, final Map<String, List<String>> sessionsFromCustomer,
                                                         final Map<String, List<View>> viewsFromSession) {
      System.out.println("Number of Views for Purchased Product by Customer");

      String currentsesh;
      String currentcust = "";
      String currentpro = "";

      for (Map.Entry<String, List<String>> entry : sessionsFromCustomer.entrySet()) {
         String key = entry.getKey();
         List<String> custseshlist;
         custseshlist = sessionsFromCustomer.get(key);
         currentcust = key;
         boolean printed = false;
         boolean printedcust = false;

         for (int k = 0; k < custseshlist.size(); k++) {
            printedcust = false;
            int countsesh = 0;
            currentsesh = custseshlist.get(k);
//            System.out.println(currentsesh);

            List<Buy> productlist;
            productlist = buysFromSession.get(currentsesh);
//            System.out.println(productlist);

            if (productlist != null) {
               for (int j = 0; j < productlist.size(); j++) {
                  countsesh = 0;
                  currentpro = productlist.get(j).getProductID();
//                  System.out.println(currentpro);

                  List<String> custseshlist2;
                  custseshlist2 = sessionsFromCustomer.get(currentcust);
                  for (int z = 0; z < custseshlist2.size(); z++){
//                     System.out.println(custseshlist2.get(z));
                     List<View> viewlist;
                     viewlist = viewsFromSession.get(custseshlist2.get(z));

                     if (viewlist != null){
                        for (int x = 0; x < viewlist.size(); x++) {
                           if ((viewlist.get(x).getProduct()).equals(currentpro)) {
                              //                           System.out.println(viewlist.get(x).getProduct() + productlist.get(j).getProductID());
                              countsesh += 1;
                              //                           System.out.println(countsesh + " " + currentcust);
                              break;
                           }
                        }

                     }
                  }
                  System.out.println(currentcust);
                  if (!printed){
                     System.out.println("\t" + currentpro + " " + countsesh);
                     printed = false;
                  }
               }
            }


//               for (int j = 0; j < customerlist.size(); j++) {
//                  if (key.equals(customerlist.get(j))) {
//                     currentcust = cust;
//                     //printed = false;
//                     for (int z = 0; z < customerlist.size(); z++) {
//
//                        List<View> viewList;
//                        viewList = viewsFromSession.get(customerlist.get(z));
//
//                        if (viewList == null){
//
//                        }
//                        else{
////                        System.out.println(viewList);
//                           for (int x = 0; x < viewList.size(); x++) {
//                              if (currentproduct.equals(viewList.get(x).getProduct())) {
//   //                              System.out.println(viewList.get(x).getProduct());
//                                 countsesh += 1;
//                                 break;
//                              }
//            }
         }
      }
   }

//
//            if (!printed) {
//               System.out.println(currentcust);
//               printed = true;
//            }
//            System.out.println("\t" + currentproduct + " " +countsesh);
//         }
//      }
//   }
//      //write this after you have figured out how to store your data
      //make sure that you understand the problem
   private static void printStatistics(final Map<String, List<String>> sessionsFromCustomer,
                                       final Map<String, List<View>> viewsFromSession,
                                       final Map<String, List<Buy>> buysFromSession )
   {
      average(buysFromSession, viewsFromSession);

      printSessionPriceDifference(buysFromSession, viewsFromSession);
      printCustomerItemViewsForPurchase(buysFromSession, sessionsFromCustomer, viewsFromSession);

      /* This is commented out as it will not work until you read
         in your data to appropriate data structures, but is included
         to help guide your work - it is an example of printing the
         data once propogated

       */
//      printOutExample(sessionsFromCustomer, viewsFromSession, buysFromSession);

   }

   /* provided as an example of a method that might traverse your
      collections of data once they are written 
      commented out as the classes do not exist yet - write them! */

   private static void printOutExample(
      final Map<String, List<String>> sessionsFromCustomer,
      final Map<String, List<View>> viewsFromSession,
      final Map<String, List<Buy>> buysFromSession) 
   {
      //for each customer, get their sessions
      //for each session compute views
      for(Map.Entry<String, List<String>> entry: 
         sessionsFromCustomer.entrySet()) 
      {
         System.out.println(entry.getKey());
         List<String> sessions = entry.getValue();
         for(String sessionID : sessions)
         {
            System.out.println("\tin " + sessionID);
            List<View> theViews = viewsFromSession.get(sessionID);
            for (View thisView: theViews)
            {
               System.out.println("\t\tviewed " + thisView.getProduct());
            }
         }
      }
   }


      //called in populateDataStructures
   private static void processFile(
      final Scanner input,
      final Map<String, List<String>> sessionsFromCustomer,
      final Map<String, List<View>> viewsFromSession,
      final Map<String, List<Buy>> buysFromSession
      /* add parameters as needed */
      )
   {
      while (input.hasNextLine())
      {
         processLine(input.nextLine(), sessionsFromCustomer, viewsFromSession, buysFromSession
            /* add arguments as needed */ );
      }
   }

      //called from main - mostly just pass through important data structures
   private static void populateDataStructures(
      final String filename,
      final Map<String, List<String>> sessionsFromCustomer,
      final Map<String, List<View>> viewsFromSession,
      final Map<String, List<Buy>> buysFromSession
      /* add parameters as needed */
      )
      throws FileNotFoundException
   {
      try (Scanner input = new Scanner(new File(filename)))
      {
         processFile(input, sessionsFromCustomer, viewsFromSession, buysFromSession
            /* add arguments as needed */ );
      }
   }

   private static String getFilename(String[] args)
   {
      if (args.length < 1)
      {
         System.err.println("Log file not specified.");
         System.exit(1);
      }

      return args[0];
   }

   public static void main(String[] args)
   {
      /* Map from a customer id to a list of session ids associated with
       * that customer.
       */
      final Map<String, List<String>> sessionsFromCustomer = new HashMap<>();
      final Map<String, List<View>> viewsFromSession = new HashMap<>();
      final Map<String, List<Buy>> buysFromSession = new HashMap<>();

      /* create additional data structures to hold relevant information */
      /* they will most likely be maps to important data in the logs */

      final String filename = getFilename(args);
 //     final String filename = "small.log";

      try
      {
         populateDataStructures(filename, sessionsFromCustomer, viewsFromSession, buysFromSession
            /* add parameters as needed */
            );
         printStatistics(sessionsFromCustomer, viewsFromSession, buysFromSession
            /* add parameters as needed */
            );
      }
      catch (FileNotFoundException e)
      {
         System.err.println(e.getMessage());
      }
   }

   public static void average(final Map<String, List<Buy>> buysFromSession, final Map<String, List<View>> viewsFromSession) {
      Double avg = 0.0;
      Double productsum = 0.0;
      Double sessionsum = 0.0;

      // go through buy list and see if session exists
      // if it does exist, dont do anything,
      // if it doesnt, add number of products viewed to product sum and add 1 to sessionsum

      for (Map.Entry<String, List<View>> entry : viewsFromSession.entrySet()) {
         List<Buy> buy = buysFromSession
                 .get(entry.getKey());
         if (buy == null) {
            List<View> view = viewsFromSession
                    .get(entry.getKey());
            productsum += view.size();
            sessionsum += 1;
         }
      }
      System.out.println("Average Views without Purchase: " +  (productsum / sessionsum));
      System.out.println("");
   }
}
