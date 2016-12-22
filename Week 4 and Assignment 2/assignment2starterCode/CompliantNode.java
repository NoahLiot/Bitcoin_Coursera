import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;

/* CompliantNode refers to a node that follows the rules (not malicious)*/
public class CompliantNode implements Node {
	
	public double p_graph;
	public double p_malicious;
	public double p_txDistribution;
	public int numRounds;
	
	public boolean[] followees;
	
	public Set<Transaction> pendingTX;
	

    public CompliantNode(double p_graph, double p_malicious, double p_txDistribution, int numRounds) {
        // IMPLEMENT THIS
    	this.p_graph = p_graph;
    	this.p_malicious = p_malicious;
    	this.p_txDistribution = p_txDistribution;
    	this.numRounds = numRounds;
    	this.pendingTX = new HashSet<Transaction>();
    }

    public void setFollowees(boolean[] followees) {
        // IMPLEMENT THIS
    	this.followees = followees;
    }

    public void setPendingTransaction(Set<Transaction> pendingTransactions) {
        // IMPLEMENT THIS
    	for (Transaction tx : pendingTransactions){
    		this.pendingTX.add(tx);
    	}
    }

    public Set<Transaction> sendToFollowers() {
        // IMPLEMENT THIS
    	return this.pendingTX;
    }

    public void receiveFromFollowees(Set<Candidate> candidates) {
        // IMPLEMENT THIS
    	for (Candidate ca: candidates){
    		this.pendingTX.add(ca.tx);
    	}
    }
}
