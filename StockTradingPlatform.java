import java.util.*;
import java.io.*;

public class StockTradingPlatform {

    // Stock Class
    static class Stock {
        private String symbol;
        private String companyName;
        private double price;

        public Stock(String symbol, String companyName, double price) {
            this.symbol = symbol;
            this.companyName = companyName;
            this.price = price;
        }

        public String getSymbol() {
            return symbol;
        }

        public String getCompanyName() {
            return companyName;
        }

        public double getPrice() {
            return price;
        }

        public void updatePrice(double price) {
            this.price = price;
        }

        @Override
        public String toString() {
            return symbol + " - " + companyName + " : ₹" + price;
        }
    }

    // Transaction Class
    static class Transaction {
        private String type;
        private String stockSymbol;
        private int quantity;
        private double price;

        public Transaction(String type, String stockSymbol,
                           int quantity, double price) {
            this.type = type;
            this.stockSymbol = stockSymbol;
            this.quantity = quantity;
            this.price = price;
        }

        @Override
        public String toString() {
            return type + " | " + stockSymbol +
                    " | Qty: " + quantity +
                    " | Price: ₹" + price;
        }
    }

    // User Class
    static class User {
        private String name;
        private double balance;
        private Map<String, Integer> portfolio;
        private List<Transaction> transactions;

        public User(String name, double balance) {
            this.name = name;
            this.balance = balance;
            portfolio = new HashMap<>();
            transactions = new ArrayList<>();
        }

        public void buyStock(Stock stock, int quantity) {

            double cost = stock.getPrice() * quantity;

            if (cost > balance) {
                System.out.println("Insufficient Balance!");
                return;
            }

            balance -= cost;

            portfolio.put(
                    stock.getSymbol(),
                    portfolio.getOrDefault(stock.getSymbol(), 0)
                            + quantity
            );

            transactions.add(
                    new Transaction(
                            "BUY",
                            stock.getSymbol(),
                            quantity,
                            stock.getPrice()
                    )
            );

            System.out.println("Stock Purchased Successfully.");
        }

        public void sellStock(Stock stock, int quantity) {

            if (!portfolio.containsKey(stock.getSymbol())) {
                System.out.println("Stock not found in portfolio.");
                return;
            }

            int owned = portfolio.get(stock.getSymbol());

            if (owned < quantity) {
                System.out.println("Not enough shares.");
                return;
            }

            balance += stock.getPrice() * quantity;

            portfolio.put(stock.getSymbol(), owned - quantity);

            if (portfolio.get(stock.getSymbol()) == 0) {
                portfolio.remove(stock.getSymbol());
            }

            transactions.add(
                    new Transaction(
                            "SELL",
                            stock.getSymbol(),
                            quantity,
                            stock.getPrice()
                    )
            );

            System.out.println("Stock Sold Successfully.");
        }

        public void showPortfolio(Map<String, Stock> marketStocks) {

            System.out.println("\n===== PORTFOLIO =====");

            double totalValue = 0;

            if (portfolio.isEmpty()) {
                System.out.println("No stocks owned.");
            }

            for (String symbol : portfolio.keySet()) {

                int qty = portfolio.get(symbol);

                Stock stock = marketStocks.get(symbol);

                double value = qty * stock.getPrice();

                totalValue += value;

                System.out.println(
                        symbol +
                        " | Shares: " + qty +
                        " | Current Price: ₹" + stock.getPrice() +
                        " | Value: ₹" + value
                );
            }

            System.out.println("\nCash Balance: ₹" + balance);
            System.out.println("Portfolio Value: ₹" + totalValue);
            System.out.println("Total Assets: ₹" + (balance + totalValue));
        }

        public void showTransactions() {

            System.out.println("\n===== TRANSACTION HISTORY =====");

            if (transactions.isEmpty()) {
                System.out.println("No transactions found.");
                return;
            }

            for (Transaction t : transactions) {
                System.out.println(t);
            }
        }

        public String getName() {
            return name;
        }

        public double getBalance() {
            return balance;
        }

        public Map<String, Integer> getPortfolio() {
            return portfolio;
        }
    }

    // Save Portfolio Data
    public static void savePortfolio(User user) {

        try (PrintWriter writer =
                     new PrintWriter(new FileWriter("portfolio.txt"))) {

            writer.println(user.getName());
            writer.println(user.getBalance());

            for (Map.Entry<String, Integer> entry :
                    user.getPortfolio().entrySet()) {

                writer.println(
                        entry.getKey() + "," + entry.getValue()
                );
            }

            System.out.println("Portfolio Saved Successfully.");

        } catch (IOException e) {
            System.out.println("Error Saving Portfolio.");
        }
    }

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        // Market Stocks
        Map<String, Stock> marketStocks = new HashMap<>();

        marketStocks.put(
                "AAPL",
                new Stock("AAPL", "Apple", 180)
        );

        marketStocks.put(
                "TSLA",
                new Stock("TSLA", "Tesla", 250)
        );

        marketStocks.put(
                "GOOG",
                new Stock("GOOG", "Google", 140)
        );

        marketStocks.put(
                "MSFT",
                new Stock("MSFT", "Microsoft", 300)
        );

        User user = new User("Investor", 100000);

        while (true) {

            System.out.println("\n===== STOCK TRADING PLATFORM =====");
            System.out.println("1. View Market Data");
            System.out.println("2. Buy Stock");
            System.out.println("3. Sell Stock");
            System.out.println("4. View Portfolio");
            System.out.println("5. Transaction History");
            System.out.println("6. Save Portfolio");
            System.out.println("7. Exit");

            System.out.print("Enter Choice: ");

            int choice = sc.nextInt();

            switch (choice) {

                case 1:

                    System.out.println("\n===== MARKET DATA =====");

                    for (Stock stock : marketStocks.values()) {
                        System.out.println(stock);
                    }

                    break;

                case 2:

                    System.out.print("Enter Stock Symbol: ");
                    String buySymbol =
                            sc.next().toUpperCase();

                    System.out.print("Enter Quantity: ");
                    int buyQty = sc.nextInt();

                    Stock buyStock =
                            marketStocks.get(buySymbol);

                    if (buyStock != null) {
                        user.buyStock(buyStock, buyQty);
                    } else {
                        System.out.println("Invalid Stock Symbol.");
                    }

                    break;

                case 3:

                    System.out.print("Enter Stock Symbol: ");
                    String sellSymbol =
                            sc.next().toUpperCase();

                    System.out.print("Enter Quantity: ");
                    int sellQty = sc.nextInt();

                    Stock sellStock =
                            marketStocks.get(sellSymbol);

                    if (sellStock != null) {
                        user.sellStock(sellStock, sellQty);
                    } else {
                        System.out.println("Invalid Stock Symbol.");
                    }

                    break;

                case 4:
                    user.showPortfolio(marketStocks);
                    break;

                case 5:
                    user.showTransactions();
                    break;

                case 6:
                    savePortfolio(user);
                    break;

                case 7:
                    System.out.println("Thank You!");
                    sc.close();
                    System.exit(0);

                default:
                    System.out.println("Invalid Choice!");
            }
        }
    }
}
