package controller.hint;

/**
 * This class allow to hold a pair of (key, value) expressing a word and its similarity in a gensim model from another word
 * @author Axel Bertrand
 */
public class GensimPair {
	/**
	 * Word from pair
	 */
	private final String key;
	/**
	 * Value of similarity from base word
	 */
	private final float similarity;

	/**
	 * @param key word
	 * @param similarity value of similarity of word from base word
	 */
	public GensimPair(String key, float similarity) {
		this.key = key;
		this.similarity = similarity;
	}

	/**
	 * @return key field value
	 */
	public String getKey() {
		return this.key;
	}

	/**
	 * @return similarity field value
	 */
	public float getSimilarity() {
		return this.similarity;
	}

	/**
	 * @return String representation of object
	 */
	@Override
	public String toString() {
		return "('" + this.key + "', " + this.similarity + ")";
	}
}
