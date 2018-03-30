//Representation of a SceneDeck
public class SceneDeck{
	private Scene[] deck;
	private int deck_index;
	
	public SceneDeck(Scene[] scenes){
		deck = scenes;
		deck_index = 0;
		shuffle();
	}

	private void shuffle(){
		int len = deck.length;
		for (int i=0; i < len; i++) {
			swap(i, (int)(Math.random()*(len-1-i))+i);
		}
	}
	
	private void swap(int i, int j) {
		Scene temp = deck[i];
		deck[i] = deck[j];
		deck[j] = temp;
	}
	
	public Scene draw(){
		Scene tmp = deck[deck_index];
		deck_index = deck_index+1%deck.length;
		return tmp;
	}
}
