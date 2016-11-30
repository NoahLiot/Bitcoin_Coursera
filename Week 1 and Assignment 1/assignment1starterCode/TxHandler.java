import java.util.ArrayList;
import java.security.PublicKey;

public class TxHandler {
	
    /**
     * Creates a public ledger whose current UTXOPool (collection of unspent transaction outputs) is
     * {@code utxoPool}. This should make a copy of utxoPool by using the UTXOPool(UTXOPool uPool)
     * constructor.
     */
	public UTXOPool publicLedger;
    public TxHandler(UTXOPool utxoPool) {
        // IMPLEMENT THIS
    	this.publicLedger = new UTXOPool(utxoPool);
    }

    /**
     * @return true if:
     * (1) all outputs claimed by {@code tx} are in the current UTXO pool, 
     * (2) the signatures on each input of {@code tx} are valid, 
     * (3) no UTXO is claimed multiple times by {@code tx},
     * (4) all of {@code tx}s output values are non-negative, and
     * (5) the sum of {@code tx}s input values is greater than or equal to the sum of its output
     *     values; and false otherwise.
     */
    public boolean isValidTx(Transaction tx) {
        // IMPLEMENT THIS
    	ArrayList<Transaction.Input> inputList = tx.getInputs();
    	double inTotal =0;
    	ArrayList<Transaction.Output> outputList = tx.getOutputs();
    	double outTotal = 0;
    	ArrayList<UTXO> claimedUTXOList = new ArrayList();
    	
    	for(int i=0; i<inputList.size();i++){
    		Transaction.Input input = inputList.get(i);
    		UTXO u = new UTXO(input.prevTxHash, input.outputIndex);
    		
    		// (1)all outputs claimed by {@code tx} are in the current UTXO pool,
    		if(!this.publicLedger.contains(u)){
    			return false;
    		}
    		
    		// (2) the signatures on each input of {@code tx} are valid,
    		Transaction.Output output = this.publicLedger.getTxOutput(u);
    		inTotal = inTotal + output.value;
    		if(!Crypto.verifySignature(output.address, tx.getRawDataToSign(i), input.signature)){
    			return false;
    		}
    		// (3) no UTXO is claimed multiple times by {@code tx}
    		if(claimedUTXOList.contains(u)){
    			return false;
    		}
    	}
    	for(int j = 0; j < outputList.size(); j++){
    		// (4) all of {@code tx}s output values are non-negative
    		outTotal = outTotal + outputList.get(j).value;
    		if(outputList.get(j).value < 0){
    			return false;
    		}
    	}
    	// (5) the sum of {@code tx}s input values is greater than or equal to the sum of its output values; and false otherwise.
    	if(inTotal < outTotal){
    		return false;
    	}
    	return true;
    }

    /**
     * Handles each epoch by receiving an unordered array of proposed transactions, checking each
     * transaction for correctness, returning a mutually valid array of accepted transactions, and
     * updating the current UTXO pool as appropriate.
     */
    public Transaction[] handleTxs(Transaction[] possibleTxs) {
        // IMPLEMENT THIS
    	ArrayList<Transaction> Txs = new ArrayList<>();
    	//Transaction [] validTxs = null;
    	int num = 0;
    	for(Transaction tx: possibleTxs){
    		if(isValidTx(tx)){
    			for(Transaction.Input in: tx.getInputs()){
    				this.publicLedger.removeUTXO(new UTXO(in.prevTxHash,in.outputIndex));
    			}
    			int index =0;
    			for(Transaction.Output out: tx.getOutputs()){
    				this.publicLedger.addUTXO(new UTXO(tx.getHash(),index++),out);
    			}
    			num++;
    			Txs.add(tx);
    		//validTxs[num] = new Transaction(tx);
    		}
    	}
    	
    	Transaction [] validTxs = new Transaction[num];
    	for(int i=0;i<num;i++){
    		validTxs[i] = Txs.get(i);
    	}
    	return validTxs;
    }

}
