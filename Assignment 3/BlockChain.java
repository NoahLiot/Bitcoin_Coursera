// Block Chain should maintain only limited block nodes to satisfy the functions
// You should not have all the blocks added to the block chain in memory 
// as it would cause a memory overflow.
import java.util.ArrayList;
import java.util.*;

public class BlockChain {
    public static final int CUT_OFF_AGE = 10;
    
    private class Node{
    	Node parent;
    	List<Node> children;
    	int height;
    	Block block;
    	UTXOPool utxoPool;
    	
    	
    	public Node(Node parent,Block block, UTXOPool up){
    		this.parent = parent;
    		this.block = block;
    		this.utxoPool = up;
    		this.children = new ArrayList<>();
    		if(parent != null){
    			parent.children.add(this);
    			this.height = parent.height+1;
    		}else{
    			this.height = 1;
    		}
    	}
    	public UTXOPool getUTXPool(){
    		return new UTXOPool(utxoPool);
    	}
    }

    private ArrayList<Node> HeadNode;
    
    private HashMap<ByteArrayWrapper,Node> HashBH;
    
    private int maxHeight;
    private Node maxHNode;
    
    private TransactionPool transactionPool;
    
    
    private Node gBlock;
    private Node prevBlock;
    
    
    
    
    /**
     * create an empty block chain with just a genesis block. Assume {@code genesisBlock} is a valid
     * block
     */
    public BlockChain(Block genesisBlock) {
        // IMPLEMENT THIS
    	Transaction coinbase = genesisBlock.getCoinbase();
    	
    	UTXOPool uPool = new UTXOPool();
    	UTXO utxoCoinbase = new UTXO(coinbase.getHash(),0);
    	uPool.addUTXO(utxoCoinbase, coinbase.getOutput(0));
    	Node gen = new Node(null,genesisBlock,uPool);
    	this.HeadNode = new ArrayList<>();
    	this.HeadNode.add(gen);
    	this.HashBH = new HashMap<>();
    	this.HashBH.put(new ByteArrayWrapper(genesisBlock.getHash()), gen);
    	this.maxHeight = 1;
    	maxHNode = gen;
    	this.transactionPool = new TransactionPool();
    	
    }

    /** Get the maximum height block */
    public Block getMaxHeightBlock() {
        // IMPLEMENT THIS
    	return maxHNode.block;
    }

    /** Get the UTXOPool for mining a new block on top of max height block */
    public UTXOPool getMaxHeightUTXOPool() {
        // IMPLEMENT THIS
    	return maxHNode.utxoPool;
    }

    /** Get the transaction pool to mine a new block */
    public TransactionPool getTransactionPool() {
        // IMPLEMENT THIS
    	return this.transactionPool;
    }

    /**
     * Add {@code block} to the block chain if it is valid. For validity, all transactions should be
     * valid and block should be at {@code height > (maxHeight - CUT_OFF_AGE)}.
     * 
     * <p>
     * For example, you can try creating a new block over the genesis block (block height 2) if the
     * block chain height is {@code <=
     * CUT_OFF_AGE + 1}. As soon as {@code height > CUT_OFF_AGE + 1}, you cannot create a new block
     * at height 2.
     * 
     * @return true if block is successfully added
     */
    public boolean addBlock(Block block) {
        // IMPLEMENT THIS
    	byte[] prevHash = block.getPrevBlockHash();
    	if (prevHash == null){
    		return false;
    	}
    	Node prevNode = this.HashBH.get(new ByteArrayWrapper(prevHash));
    	if (prevNode == null){
    		return false;
    	}
    	TxHandler handler = new TxHandler(prevNode.getUTXPool());
    	ArrayList<Transaction> TXs = block.getTransactions();
    	Transaction[] validTXs = new Transaction[TXs.size()];
    	validTXs = handler.handleTxs(TXs.toArray(validTXs));
    	if (validTXs.length < TXs.size()){
    		return false;
    	}
    	

    	
    	UTXOPool pool = handler.getUTXOPool();
    	Transaction coinbase = block.getCoinbase();
    	pool.addUTXO(new UTXO(coinbase.getHash(),0), coinbase.getOutput(0));
    	Node newNode = new Node(prevNode,block,pool);
    	this.HashBH.put(new ByteArrayWrapper(block.getHash()), newNode);
    	
    	for(Transaction tx :block.getTransactions()){
    		this.transactionPool.removeTransaction(tx.getHash());
    	}
    	
    	if( (prevNode.height +1) > this.maxHeight){
    		this.maxHeight = prevNode.height + 1;
    		this.maxHNode = newNode;
    	}
    	
    	if (newNode.height > this.maxHeight - CUT_OFF_AGE){
    		return true;
    	}else{
    		return false;
    	}
    	
    	
    }

    /** Add a transaction to the transaction pool */
    public void addTransaction(Transaction tx) {
        // IMPLEMENT THIS
    	this.transactionPool.addTransaction(tx);
    }
}