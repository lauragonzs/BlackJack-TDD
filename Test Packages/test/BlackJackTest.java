package test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Stream;
import org.junit.Test;

import static test.BlackJackTest.Card.*;
import static org.junit.Assert.assertEquals;

public class BlackJackTest{
         
    @Test
    public void test_hand_value_with_one_card() {
        assertEquals(3, createHand(_3).value());
        assertEquals(10, createHand(_10).value());
        assertEquals(10, createHand(Jack).value());
        assertEquals(10, createHand(Queen).value());
        assertEquals(10, createHand(King).value());
        assertEquals(11, createHand(Ace).value());
    }
    
    @Test
    public void test_hand_value_with_two_cards() {
        assertEquals(8, createHand(_3, _5).value());
        assertEquals(12, createHand(Ace, Ace).value());     
    }
    
    @Test
    public void test_hand_is_black_jack() {
        assertEquals(false, createHand(_3, _5).isBlackJack());        
        assertEquals(true, createHand(Ace, Jack).isBlackJack());
        assertEquals(true, createHand(Ace, King).isBlackJack());
        assertEquals(true, createHand(Ace, Queen).isBlackJack());
        assertEquals(true, createHand(_10, Ace).isBlackJack());
        assertEquals(false, createHand(_10, _10, Ace).isBlackJack());
    }
    
    @Test
    public void test_getWinners() {
        String[] winners = {"Player1"};
        Player p1 = createPlayer("Player1", createHand(Jack, Ace));
        Player p2 = createPlayer("Player2", createHand(_10, _5, _6));
        Player p3 = createPlayer("Player3", createHand(_3, _6, Ace, _3, Ace, King));
        Hand cr = createHand(_9, _7);
        Card[] deckArray = {_5, _4, King, _2};
        Deck deck = createDeck(deckArray);
        assertEquals(Arrays.toString(winners), Arrays.toString(getWinners(p1,p2,p3,cr,deck))); 
        
        String[] winners2 = {"Player1", "Player3"};
        p1 = createPlayer("Player1", createHand(_10, King));
        p2 = createPlayer("Player2", createHand(_10, _2, _4));
        p3 = createPlayer("Player3", createHand(_8, _8, _5));
        cr = createHand(_5, _10);
        Card[] deckArray2 = {Ace, _3, King, _2};
        deck = createDeck(deckArray2);
        assertEquals(Arrays.toString(winners2), Arrays.toString(getWinners(p1,p2,p3,cr,deck)));     
        
        String[] winners3 = {"Crupier"};
        p1 = createPlayer("Player1", createHand(_10, King));
        p2 = createPlayer("Player2", createHand(_10, _2, _6));
        p3 = createPlayer("Player3", createHand(_8, _8, _5));
        cr = createHand(Ace, _10, _10);
        Card[] deckArray3 = {Ace, _3, King, _2};
        deck = createDeck(deckArray3);
        assertEquals(Arrays.toString(winners3), Arrays.toString(getWinners(p1,p2,p3,cr,deck))); 
        
        String[] winners4 = {"Crupier"};
        p1 = createPlayer("Player1", createHand(_10, _3));
        p2 = createPlayer("Player2", createHand(_10, _2));
        p3 = createPlayer("Player3", createHand(_8, _8));
        cr = createHand(_10, _10);
        Card[] deckArray4 = {_3, King, _2};
        deck = createDeck(deckArray4);
        assertEquals(Arrays.toString(winners4), Arrays.toString(getWinners(p1,p2,p3,cr,deck)));
        
        String[] winners5 = {"Player2", "Player3"};
        p1 = createPlayer("Player1", createHand(_10));
        p2 = createPlayer("Player2", createHand(_10, _2, _6));
        p3 = createPlayer("Player3", createHand(_8, _8, _5));
        cr = createHand(_2, _10);
        Card[] deckArray5 = {_2};
        deck = createDeck(deckArray5);
        assertEquals(Arrays.toString(winners5), Arrays.toString(getWinners(p1,p2,p3,cr,deck)));  
    }
    
    
    public String[] getWinners(Player p1, Player p2, Player p3, Hand crupierHand, Deck deck){
        Crupier crupier = createCrupier(crupierHand, deck);
        Player[] players = {p1, p2, p3};
        ArrayList<String> winnersAL = new ArrayList<>();
        
        boolean bj = anyBlackJack(p1.getHand(), p2.getHand(), p3.getHand());
        for (Player player : players) {
            if (handWinsCrupier(player.getHand(), crupier) && bj && player.getHand().isBlackJack()) winnersAL.add(player.name());
            else if(handWinsCrupier(player.getHand(), crupier) && !bj) winnersAL.add(player.name());
        }
        if((crupier.getHand().isBlackJack()) || (winnersAL.isEmpty())) winnersAL.add("Crupier");
        
        String winnersArray[] = new String[winnersAL.size()];
        for (int i = 0; i < winnersArray.length; i++)
            winnersArray[i] = winnersAL.get(i);
        return winnersArray;
    }
    
    private boolean handWinsCrupier(Hand hand, Crupier crupier){
        return ((hand.value() > crupier.getHand().value()) ||
                (hand.isBlackJack() && !crupier.getHand().isBlackJack()) ||
                (crupier.getHand().isBust() && !hand.isBust()));
    }
    
    private boolean anyBlackJack(Hand... hands){
        for (Hand hand : hands)
            if(hand.isBlackJack()) return true;
        return false;
    }
    
    
    public Player createPlayer(String name, Hand hand) {
        return new Player() {
            @Override
            public String name() {
                return name;
            }

            @Override
            public Hand getHand() {
                return hand;
            }
        };
    }
    
    public interface Player {
        String name();
        Hand getHand();
    }
    
    
    public Crupier createCrupier(Hand hand, Deck deck) {
        return new Crupier() {          
            @Override
            public Hand getHand() {
                while (hand.value() < 17 && !deck.isEmpty()){
                    hand.addCard(deck.takeCard());
                }
                return hand;
            }
        };
    }
    
    public interface Crupier {
        Hand getHand();
    }
    
    public Deck createDeck(Card... cards) {
        return new Deck() {
            private int card = 0;
            
            @Override
            public Card takeCard() {
                return cards[card++];
            }
            
            @Override
            public boolean isEmpty() {
                return card+1 == cards.length;
            }
        };
    }
    
    public interface Deck {
        Card takeCard();
        boolean isEmpty();
    }
    

    public Hand createHand(Card... cardsArray) {
        return new Hand() {
            private Card[] cards = cardsArray.clone();
                                
            @Override
            public int value() {
                int sum = 0;
                for (Card card : cards)
                    sum += card.value();
                if(this.containsAce() && this.canUseAceExtendedValue(sum)) sum += 10;
                return sum;
            }

            private boolean canUseAceExtendedValue(int sum) {
                return sum <= 11 && containsAce();
            }

            private boolean containsAce() {
                return Stream.of(cards).anyMatch(c->c==Ace);
            }

            @Override
            public boolean isBlackJack() {
                return value() == 21 && cards.length == 2;
            }

            @Override
            public boolean isBust() {                
                return value() > 21;
            }
            
            @Override
            public void addCard(Card card){
                Card[] cards2 = Arrays.copyOf(cards, cards.length + 1);
                cards2[cards.length] = card;
                cards = cards2;
            }

        };
    }

    public interface Hand {
        public int value();
        public boolean isBlackJack();
        public boolean isBust();
        public void addCard(Card card);

    }
    
    public enum Card {
        _2, _3, _4, _5, _6, _7, _8, _9, _10, Jack, Queen, King, Ace;

        private boolean isFace() {
            return this == King || this == Queen || this == Jack;
        }

        private boolean isAce() {
            return this == Ace;
        }

        private int value() {
            if (isAce()) return 1;
            if (isFace()) return 10;
            return ordinal() + 2;
        }
    }
    
}