import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Scanner;


class Card{
    private int number;
    private String color;
    public Card(int number,String color){
        this.color = color;
        this.number = number;
    }
    public int getNumber() {
        return number;
    }

    public String getColor() {
        return color;
    }
    @Override
    public boolean equals(Object o) {
        if(this==o)
            return true;
        Card card = (Card)o;
        return this.number == card.getNumber() && this.color.equals(card.getColor());
    }
}


class Section{
    private ArrayList<Card> cards;
    public Section(){
        cards = new ArrayList<>();
    }

    public void addCard(Card card) {
        cards.add(card);
    }

    public void deleteCard(){
        cards.remove(cards.size()-1);
    }

    public ArrayList<Card> getCards() {
        return cards;
    }

    public void showSection(){
        for(Card card : cards){
            System.out.print(card.getNumber()+card.getColor()+"\t");
        }
    }
    @Override
    public boolean equals(Object o) {
        if(this==o){
            return true;
        }
        Section section = (Section)o;
        return section.getCards().equals(this.cards);
    }
}


class State{
    private ArrayList<Section> sections;
    private final int height;
    private State parent;
    private String changedState;
    public State(int height,State parent){
        sections = new ArrayList<>();
        this.height = height;
        this.parent = parent;
    }

    public void setChangedState(String changedState) {
        this.changedState = changedState;
    }

    public int getHeight() {
        return height;
    }

    public String getChangedState() {
        return changedState;
    }

    public State getParent() {
        return parent;
    }

    public void addSection(Section section){
        sections.add(section);
    }

    public ArrayList<Section> getSections() {
        return sections;
    }

    public void showState(){
        System.out.println("new State");
        for(Section section : sections){
            if(section.getCards().size()!=0) {
                section.showSection();
                System.out.println();
            }
            else {
                System.out.println("#");
            }
        }
    }
    @Override
    public boolean equals(Object o) {
        if(this==o){
            return true;
        }
        State section = (State) o;
        return section.getSections().equals(this.sections);
    }
}
//=================================================================================
class Node{
    private final int k;
    private final int n;
    public static int nodesExpanded=0;
    public static int nodesCreated=0;
    LinkedList<State> frontier;
    public Node(State state,int k,int n){
        this.k = k;
        this.n = n;
        frontier = new LinkedList<>();
        frontier.add(state);
    }
//this method create a new child from his parent
    private State makeNewSate(State state,int i,int j){
        State copyState = new State(state.getHeight()+1,state);
        ArrayList<Section> sections = (state.getSections());
        for(Section section:sections){
            Section newSection = new Section();
            ArrayList<Card> cards = section.getCards();
            for(Card card:cards){
                Card newCard = new Card(card.getNumber(),card.getColor());
                newSection.addCard(newCard);
            }
            copyState.addSection(newSection);
        }
        ArrayList<Card> temp = sections.get(i).getCards();
        copyState.getSections().get(j).addCard(temp.get(temp.size()-1));
        copyState.getSections().get(i).deleteCard();
        copyState.setChangedState("move "+temp.get(temp.size()-1).getNumber()+temp.get(temp.size()-1).getColor()+" from column "+ (i+1) + " to column "+ (j+1));
        return copyState;
    }

    public boolean action(int height){
        Card selectCard;
        Card currentCard;
        while (this.frontier.size()>0) {
            State currentState = this.frontier.pop();
            if(checkGoal(currentState)){
                nodesExpanded++;
                showResult(currentState);
                return true;
            }
//            this loop check all possible moves for creating new node
            if(currentState.getHeight()>=height)
                continue;
            nodesExpanded++;
            for (int i = 0; i < k; i++) {
                if (currentState.getSections().get(i).getCards().size() == 0)
                    continue;
                ArrayList<Card> temp = currentState.getSections().get(i).getCards();
                selectCard = temp.get(temp.size()-1);
                for (int j = 0; j < k; j++) {
                    if (i == j)
                        continue;
                    Section section = currentState.getSections().get(j);
                    if (section.getCards().size() == 0) {
                        checkAndAdd(currentState,i,j);
                    } else {
                        currentCard = section.getCards().get(section.getCards().size()-1);
                        if (selectCard.getNumber() < currentCard.getNumber()) {
                            checkAndAdd(currentState,i,j);
                        }
                    }
                }
            }
        }
        return false;
    }
    //check frontier and create a new node
    private void checkAndAdd(State currentState,int i,int j){
        State newSate;
        newSate = this.makeNewSate(currentState, i, j);
            frontier.push(newSate);
            nodesCreated++;
    }
//this method show final state,steps and other staffs
    private void showResult(State currentState){
        State tmp = currentState;
        System.out.println("find");
        currentState.showState();
        System.out.println("depth: "+ currentState.getHeight());
        System.out.println("steps");
        ArrayList<String> steps = new ArrayList<>();
        while(tmp.getParent()!=null){
            steps.add(tmp.getChangedState());
            tmp = tmp.getParent();
        }
        for(int j = steps.size()-1;j>=0;j--){
            System.out.println(steps.get(j));
        }
        System.out.println("node created: "+nodesCreated);
        System.out.println("node expanded: "+nodesExpanded);
    }

    public boolean checkGoal(State state){
        String color;
        ArrayList<Section> sections = state.getSections();
        for(Section section:sections) {
            ArrayList<Card> cards = section.getCards();
            if (cards.size() > 0) {
                color = cards.get(0).getColor();
                for (Card card : cards) {
                    if (!color.equals(card.getColor())) {
                        return false;
                    }
                }
                int number = cards.get(0).getNumber();
                for (Card card : cards) {
                    if (card.getNumber() > number) {
                        return false;
                    }
                    number = card.getNumber();
                }
            }
        }
        return true;
    }
}



//======================================================================================
public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        String cardLine;
        String[] cards;
        int firstHeight = 0;
        int k = sc.nextInt();
//        color count
        int m = sc.nextInt();
//        number count
        int n = sc.nextInt();
        sc.nextLine();
        Section[] section = new Section[k];
        State state = new State(0,null);
        state.setChangedState("first state");
        for(int i=0; i<k; i++){
            section[i] = new Section();
            cardLine = sc.nextLine();
            if(cardLine.equals("#")){
                section[i] = new Section();
                state.addSection(section[i]);
                continue;
            }
            cards = cardLine.split(" ");
            for (String card : cards) {
                int number = Integer.parseInt(card.substring(0, card.length()-1));
                String color = card.substring(card.length()-1,card.length());
                Card newCard = new Card(number, color);
                section[i].addCard(newCard);
            }
            state.addSection(section[i]);
        }
        while (true) {
            Node node = new Node(state, k, n);
            Node.nodesCreated++;
            if(node.action(firstHeight))
                break;
            else
                firstHeight++;
        }
    }
}
