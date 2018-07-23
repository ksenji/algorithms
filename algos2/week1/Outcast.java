public class Outcast {
  private final WordNet wordnet;

  public Outcast(WordNet wordnet) {
    this.wordnet = wordnet;
  }

  public String outcast(String[] nouns) {
    String outcast = null;
    int max = 0;
    for (String nounA : nouns) {
      int distance = 0;
      for (String nounB : nouns) {
        if (!nounA.equals(nounB)) {
          int dis = wordnet.distance(nounA, nounB);
          if (dis != -1) {
            distance += dis;
          }
        }
      }
      if (distance >= max) {
        max = distance;
        outcast = nounA;
      }
    }
    return outcast;
  }
}
