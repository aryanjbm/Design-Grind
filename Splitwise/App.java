package Splitwise;

import java.util.Map;
import java.util.PriorityQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.List;
import java.util.ArrayList;

interface DebtSimplificationStrategy{
    List<String> simplify(Map<String, Double> balances);
}

record Balance(String userId,double amount){}

class GreedyDebtSimplificationStrategy implements DebtSimplificationStrategy{
    @Override
    public List<String> simplify(Map<String, Double> balances){
        // Default simplification logic can be implemented here
        // For now, it just returns the balances as is
        PriorityQueue<Balance> creditors = new PriorityQueue<>((a,b) -> Double.compare(b.amount(),a.amount())); // Positive balances // Max-heap
        PriorityQueue<Balance> debtors = new PriorityQueue<>((a,b) -> Double.compare(a.amount(),b.amount())); // Negative balances // Min-heap

        for(Map.Entry<String, Double> entry : balances.entrySet()){
            if(entry.getValue() > 0){
                creditors.add(new Balance(entry.getKey(), entry.getValue()));
            }
            else if(entry.getValue() < 0){
                debtors.add(new Balance(entry.getKey(), entry.getValue()));
            }
        }

        List<String> transactions = new ArrayList<>();

        while(!creditors.isEmpty() && !debtors.isEmpty()){
            Balance creditor = creditors.poll();
            Balance debtor = debtors.poll();

            double settledAmount = Math.min(creditor.amount(), -debtor.amount());

            transactions.add(debtor.userId() + " pays " + settledAmount + " to " + creditor.userId());

            double newCreditorAmount = creditor.amount() - settledAmount;
            double newDebtorAmount = debtor.amount() + settledAmount;

            if(newCreditorAmount > 0){
                creditors.add(new Balance(creditor.userId(), newCreditorAmount));
            }
            if(newDebtorAmount < 0){
                debtors.add(new Balance(debtor.userId(), newDebtorAmount));
            }
        }
        return transactions;  
    }
}

class Expense{
    String id;
    double amount;
    String paidBy;
    String[] splitBetween;

    public Expense(String id, double amount, String paidBy, String[] splitBetween){
        this.id = id;
        this.amount = amount;
        this.paidBy = paidBy;
        this.splitBetween = splitBetween;
    }
}


class SplitExpenseManager{

    private Map<String, Double> balances;

    public SplitExpenseManager(){
        balances = new ConcurrentHashMap<>();
    }

    public void addExpense(Expense expense){
        double splitAmount = expense.amount / expense.splitBetween.length;
        balances.put(expense.paidBy, balances.getOrDefault(expense.paidBy, 0.0) + expense.amount);
        for(String user : expense.splitBetween){
            balances.put(user, balances.getOrDefault(user, 0.0) - splitAmount);
        }
        
    }
    public void simplifyDebts(DebtSimplificationStrategy strategy){
        // Simplification logic can be implemented here
        for(Map.Entry<String, Double> entry : balances.entrySet()){
            System.out.println(entry.getKey() + " : " + entry.getValue());
        }
        List<String> transactions = strategy.simplify(balances);
        
        System.out.println("--- Simplied Settlements ---");
        for (String t : transactions) {
            System.out.println(t);
        }
    }







}
public class App {
    public static void main(String[] args) {
        System.out.println("Welcome to Splitwise Application!");
        SplitExpenseManager manager = new SplitExpenseManager();

        String[] group = {"Alice", "Bob", "Charlie"};
        
        manager.addExpense(new Expense("1", 300, "Alice", group)); // Each share 100. Alice +200, B -100, C -100
        manager.addExpense(new Expense("2", 100, "Bob", new String[]{"Bob", "Charlie"})); // Each share 50. Bob +50, C -50
        
        // Final State expected:
        // Alice: +200 (Net)
        // Bob: -100 (from Exp1) + 50 (Net from Exp2) = -50
        // Charlie: -100 (from Exp1) - 50 (from Exp2) = -150
        
        // Simplification:
        // Charlie (-150) pays Alice (+200) -> Pays 150. Alice remaining +50.
        // Bob (-50) pays Alice (+50) -> Pays 50.
        // Done.
        
        manager.simplifyDebts(new GreedyDebtSimplificationStrategy());
    }
    
}
