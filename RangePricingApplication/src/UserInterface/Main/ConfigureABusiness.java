/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package UserInterface.Main;

import MarketingManagement.MarketingPersonDirectory;
import MarketingManagement.MarketingPersonProfile;
import TheBusiness.Business.Business;
import TheBusiness.CustomerManagement.CustomerDirectory;
import TheBusiness.CustomerManagement.CustomerProfile;
import TheBusiness.OrderManagement.MasterOrderList;
import TheBusiness.OrderManagement.Order;
import TheBusiness.OrderManagement.OrderItem;
import TheBusiness.Personnel.Person;
import TheBusiness.Personnel.PersonDirectory;
import TheBusiness.ProductManagement.Product;
import TheBusiness.ProductManagement.ProductCatalog;
import TheBusiness.SalesManagement.SalesPersonDirectory;
import TheBusiness.SalesManagement.SalesPersonProfile;
import TheBusiness.Supplier.Supplier;
import TheBusiness.Supplier.SupplierDirectory;
import TheBusiness.UserAccountManagement.UserAccount;
import TheBusiness.UserAccountManagement.UserAccountDirectory;
import com.github.javafaker.Faker;

/**
 *
 * @author kal bugrara
 */
class ConfigureABusiness {

    static Business initialize() {
    Business business = new Business("Xerox");
    
    // Initialize Faker for random names
    Faker faker = new Faker();
    
    // =================================================================
    // STEP 1: CREATE PERSONS (Sales, Marketing, Admin, Customers)
    // =================================================================
    PersonDirectory persondirectory = business.getPersonDirectory();
    
    // Create 1 sales person
    Person xeroxsalesperson001 = persondirectory.newPerson("Xerox Sales Rep");
    
    // Create 1 marketing person (YOU will use this login)
    Person xeroxmarketingperson001 = persondirectory.newPerson("Xerox Marketing Manager");
    
    // Create 1 admin person
    Person xeroxadminperson001 = persondirectory.newPerson("Xerox Admin");
    
    // Create 300 customers with random names
    Person[] customerPersons = new Person[300];
    for (int i = 0; i < 300; i++) {
        String customerName = faker.company().name(); // Random company name
        customerPersons[i] = persondirectory.newPerson(customerName);
    }
    
    System.out.println("✅ Created 303 persons (3 employees + 300 customers)");
    
    // =================================================================
    // STEP 2: CREATE CUSTOMER PROFILES
    // =================================================================
    CustomerDirectory customerdirectory = business.getCustomerDirectory();
    CustomerProfile[] customerProfiles = new CustomerProfile[300];
    
    for (int i = 0; i < 300; i++) {
        customerProfiles[i] = customerdirectory.newCustomerProfile(customerPersons[i]);
    }
    
    System.out.println("✅ Created 300 customer profiles");
    
    // =================================================================
    // STEP 3: CREATE SALES & MARKETING PROFILES
    // =================================================================
    SalesPersonDirectory salespersondirectory = business.getSalesPersonDirectory();
    SalesPersonProfile salespersonprofile = salespersondirectory.newSalesPersonProfile(xeroxsalesperson001);
    
    MarketingPersonDirectory marketingpersondirectory = business.getMarketingPersonDirectory();
    MarketingPersonProfile marketingpersonprofile = marketingpersondirectory.newMarketingPersonProfile(xeroxmarketingperson001);
    
    System.out.println("✅ Created sales & marketing profiles");
    
    // =================================================================
    // STEP 4: CREATE USER ACCOUNTS (FOR LOGIN)
    // =================================================================
    UserAccountDirectory uadirectory = business.getUserAccountDirectory();
    UserAccount ua1 = uadirectory.newUserAccount(salespersonprofile, "sales", "XXXX");
    UserAccount ua2 = uadirectory.newUserAccount(marketingpersonprofile, "marketing", "XXXX");
    
    System.out.println("✅ Created user accounts");
    System.out.println("   Login as: marketing / XXXX");
    
    // =================================================================
    // STEP 5: CREATE 50 SUPPLIERS WITH 50 PRODUCTS EACH
    // =================================================================
    SupplierDirectory supplierdirectory = business.getSupplierDirectory();
    Product[][] allProducts = new Product[50][50]; // Store for later use in orders
    
    for (int i = 0; i < 50; i++) {
        // Create supplier with random name
        String supplierName = faker.company().name();
        Supplier supplier = supplierdirectory.newSupplier(supplierName);
        ProductCatalog catalog = supplier.getProductCatalog();
        
        // Create 50 products for this supplier
        for (int j = 0; j < 50; j++) {
            // Generate random product name
            String productName = faker.commerce().productName();
            
            // Generate random prices
            int floorPrice = 1000 + (int)(Math.random() * 5000);      // 1,000 to 5,999
            int ceilingPrice = floorPrice + 5000 + (int)(Math.random() * 10000); // 6,000 to 15,999 above floor
            int targetPrice = floorPrice + (ceilingPrice - floorPrice) / 2;      // Midpoint
            
            // Create product and store it
            Product product = catalog.newProduct(productName, floorPrice, ceilingPrice, targetPrice);
            allProducts[i][j] = product;
        }
        
        // Progress indicator (print every 10 suppliers)
        if ((i + 1) % 10 == 0) {
            System.out.println("   📦 Created " + (i + 1) + " suppliers...");
        }
    }
    
    System.out.println("✅ Created 50 suppliers with 2,500 total products");
    
    // =================================================================
    // STEP 6: CREATE ORDERS (1-3 orders per customer)
    // =================================================================
    MasterOrderList masterorderlist = business.getMasterOrderList();
    int totalOrders = 0;
    int totalItems = 0;
    
    for (int custIndex = 0; custIndex < 300; custIndex++) {
        // Each customer gets 1-3 orders
        int numOrders = 1 + (int)(Math.random() * 3); // Random: 1, 2, or 3
        
        for (int orderNum = 0; orderNum < numOrders; orderNum++) {
            // Create order
            Order order = masterorderlist.newOrder(customerProfiles[custIndex], salespersonprofile);
            
            // Add 1-10 items to this order
            int numItems = 1 + (int)(Math.random() * 10); // Random: 1 to 10
            
            for (int itemNum = 0; itemNum < numItems; itemNum++) {
                // Pick random product from any supplier
                int randomSupplier = (int)(Math.random() * 50);
                int randomProduct = (int)(Math.random() * 50);
                Product selectedProduct = allProducts[randomSupplier][randomProduct];
                
                // Generate actual price (randomly above or below target)
                int targetPrice = selectedProduct.getTargetPrice();
                int floorPrice = selectedProduct.getFloorPrice();
                int ceilingPrice = selectedProduct.getCeilingPrice();
                
                // 60% chance above target, 40% chance below target (realistic distribution)
                int actualPrice;
                if (Math.random() < 0.6) {
                    // Above target (between target and ceiling)
                    actualPrice = targetPrice + (int)(Math.random() * (ceilingPrice - targetPrice));
                } else {
                    // Below target (between floor and target)
                    actualPrice = floorPrice + (int)(Math.random() * (targetPrice - floorPrice));
                }
                
                // Random quantity: 1-5
                int quantity = 1 + (int)(Math.random() * 5);
                
                // Add item to order
                order.newOrderItem(selectedProduct, actualPrice, quantity);
                totalItems++;
            }
            
            totalOrders++;
        }
        
        // Progress indicator (print every 50 customers)
        if ((custIndex + 1) % 50 == 0) {
            System.out.println("   🛒 Created orders for " + (custIndex + 1) + " customers...");
        }
    }
    
    System.out.println("✅ Created " + totalOrders + " orders with " + totalItems + " total items");
    System.out.println("\n🎉 DATA GENERATION COMPLETE!");
    System.out.println("================================================");
    System.out.println("Summary:");
    System.out.println("  - 50 Suppliers");
    System.out.println("  - 2,500 Products");
    System.out.println("  - 300 Customers");
    System.out.println("  - " + totalOrders + " Orders");
    System.out.println("  - " + totalItems + " Order Items");
    System.out.println("================================================");
    
    return business;
}

}
