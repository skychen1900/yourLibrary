package com.mycompany.poll;

/**
 * This class is a simple bean used for the quick poll example. It
 * just keep track of three alternative votes and the total of
 * votes.
 *
 * @author Hans Bergsten, Gefion Software <hans@gefionsoftware.com>
 * @version 1.0
 */
public class PollBean {
    private int vote1, vote2, vote3, total;
    private boolean showScore = false;
    public String vote1() {
	vote1++;
	total++;
	showScore = true;
	return null;
    }
    public String vote2() {
	vote2++;
	total++;
	showScore = true;
	return null;
    }
    public String vote3() {
	vote3++;
	total++;
	showScore = true;
	return null;
    }
    public boolean getShowScore() {
	return showScore;
    }
    public int getTotal() {
	return total;
    }
    public double getVote1Score() {
	return (int) Math.round(((double) vote1 / (double) total) * 100);
    }
    public double getVote2Score() {
	return (int) Math.round(((double) vote2 / (double) total) * 100);
    }
    public double getVote3Score() {
	return (int) Math.round(((double) vote3 / (double) total) * 100);
    }
}
