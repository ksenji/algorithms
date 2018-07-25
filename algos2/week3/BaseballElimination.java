import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.princeton.cs.algs4.FlowEdge;
import edu.princeton.cs.algs4.FlowNetwork;
import edu.princeton.cs.algs4.FordFulkerson;
import edu.princeton.cs.algs4.In;

public class BaseballElimination {
  private final Map<String, Integer> index;

  private final Team[] teams;

  public BaseballElimination(String filename) {
    In in = new In(requireNonNull(filename, () -> "filename cannot be null"));
    int t = in.readInt();
    teams = new Team[t];
    index = new HashMap<>();
    int max = 0;
    int maxId = 0;
    String maxName = null;
    for (int i = 0; i < t; i++) {
      in.readLine();
      String name = in.readString();
      int wins = in.readInt();
      int losses = in.readInt();
      int remaining = in.readInt();

      if (max < wins) {
        max = wins;
        maxName = name;
        maxId = i;
      }

      int[] games = new int[t];
      for (int j = 0; j < games.length; j++) {
        games[j] = in.readInt();
      }
      Team team = new Team(i, name, wins, losses, remaining, games);
      teams[i] = team;
      index.put(name, i);
    }

    for (int i = 0; i < t; i++) {
      if (maxId != i) {
        Team x = teams[i];
        if (x.wins + x.remaining < max) {
          x.computed = true;
          x.certificateOfElimination = Collections.singletonList(maxName);
        }
      }
    }
  }

  public int numberOfTeams() {
    return teams.length;
  }

  public Iterable<String> teams() {
    List<String> names = new ArrayList<>(teams.length);
    for (Team team : teams) {
      names.add(team.name);
    }
    return names;
  }

  public int wins(String team) {
    return lookup(team).wins;
  }

  public int losses(String team) {
    return lookup(team).losses;
  }

  public int remaining(String team) {
    return lookup(team).remaining;
  }

  public int against(String team1, String team2) {
    Team t1 = lookup(team1);
    Team t2 = lookup(team2);
    return t1.games[t2.id];
  }

  public boolean isEliminated(String team) {
    return certificateOfElimination(team) != null;
  }

  public Iterable<String> certificateOfElimination(String team) {
    Team x = lookup(team);
    if (!x.computed) {
      int size = numberOfTeams();
      int v = 1 + size * (size - 1) / 2;

      int s = v++;
      int t = v++;

      int xId = x.id;
      int xWins = x.wins + x.remaining;

      FlowNetwork network = new FlowNetwork(v);
      int k = size;
      int totalGames = 0;
      for (int i = 0; i < size; i++) {
        if (i != xId) {
          network.addEdge(new FlowEdge(i, t, xWins - teams[i].wins));
          for (int j = i + 1; j < size; j++) {
            if (j != xId) {
              int games = teams[i].games[j];
              network.addEdge(new FlowEdge(k, i, Double.POSITIVE_INFINITY));
              network.addEdge(new FlowEdge(k, j, Double.POSITIVE_INFINITY));
              network.addEdge(new FlowEdge(s, k, games));
              totalGames += games;
              k++;
            }
          }
        }
      }

      FordFulkerson ff = new FordFulkerson(network, s, t);
      double gamesLeft = totalGames - ff.value();
      if (gamesLeft != 0) {
        List<String> certificateOfElimination = new ArrayList<>();
        for (int i = 0; i < size; i++) {
          if (i != xId && ff.inCut(i)) {
            certificateOfElimination.add(teams[i].name);
          }
        }
        x.certificateOfElimination = certificateOfElimination;
      }
      x.computed = true;
    }
    return x.certificateOfElimination;
  }

  private Team lookup(String team) {
    return teams[requireNonNull(index.get(team), () -> String.format("%s is not a valid team", team))];
  }

  private static <T> T requireNonNull(T t, ExceptionMessageBuilder builder) {
    if (t == null) {
      throw new IllegalArgumentException(builder.build());
    }
    return t;
  }

  @FunctionalInterface
  private interface ExceptionMessageBuilder {
    String build();
  }

  private static final class Team {

    private final int id;

    private final String name;

    private final int wins;

    private final int losses;

    private final int remaining;

    private final int[] games;

    private boolean computed;

    private List<String> certificateOfElimination;

    Team(int id, String name, int wins, int losses, int remaining, int[] games) {
      this.id = id;
      this.name = name;
      this.wins = wins;
      this.losses = losses;
      this.remaining = remaining;
      this.games = games;
    }
  }
}
