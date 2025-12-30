package SnaknLadder;

import java.util.Map;

interface Artifact{
    int getNextPosition(int current, int diceRoll);
}

class Mine implements Artifact{
    private int penalty;
    public Mine(int penalty){
        this.penalty = penalty;
    }
    @Override
    public int getNextPosition(int current, int diceRoll){
        return Math.max(0, current - this.penalty);
    }
}

class Ladder implements Artifact{
    private int jump;
    public Ladder(int jump){
        this.jump = jump;
    }
    @Override
    public int getNextPosition(int current, int diceRoll){
        return Math.max(0, current + this.jump);
    }
}

class Trampolin implements Artifact{
    @Override
    public int getNextPosition(int current, int diceRoll){
        return Math.max(0, current + diceRoll);
    }
}


class GameManager{
    private int size;
    private int[] currentPositions;
    private int currentPlayerIndex = 0;
    private Map<Integer, Artifact> artifacts;
    private boolean isGameOver = false;
    public GameManager(int size,int players, Map<Integer, Artifact> artifacts){
        this.size = size;
        this.currentPositions = new int[players]; // Initialize all players at position 0
        this.artifacts = artifacts;

    }



    public void playGame(){
        // Game logic to be implemented
        if(isGameOver) return;
        this.currentPlayerIndex = (this.currentPlayerIndex + 1) % this.currentPositions.length;
        int roll = rollDice();
        if(this.currentPositions[this.currentPlayerIndex] + roll <= this.size){

            
            this.currentPositions[this.currentPlayerIndex] += roll;
            int pos = this.currentPositions[this.currentPlayerIndex];
            if(this.artifacts.containsKey(pos)){
                Artifact artifact = this.artifacts.get(pos);
                this.currentPositions[this.currentPlayerIndex] = artifact.getNextPosition(pos,roll);
            }
        }
        if(this.currentPositions[this.currentPlayerIndex] == this.size){
            System.out.println("Player " + (this.currentPlayerIndex + 1) + " wins!");
        }
    }

    private int rollDice(){
        return (int)(Math.random() * 6) + 1;
    }

    



}
public class App {
    public static void main(String[] args) {
        System.out.println("Welcome to Snakes and Ladders!");
        //Game game = new Game();
        // game.start();
    }
    
}
