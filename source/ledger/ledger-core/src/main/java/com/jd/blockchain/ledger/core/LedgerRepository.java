package com.jd.blockchain.ledger.core;

import java.io.Closeable;

import com.jd.blockchain.crypto.HashDigest;
import com.jd.blockchain.ledger.LedgerAdminInfo;
import com.jd.blockchain.ledger.LedgerBlock;

public interface LedgerRepository extends Closeable {

	/**
	 * 账本哈希，这是账本的唯一标识；
	 * 
	 * @return
	 */
	HashDigest getHash();

	/**
	 * 最新区块高度；
	 * 
	 * @return
	 */
	long getLatestBlockHeight();

	/**
	 * 最新区块哈希；
	 * 
	 * @return
	 */
	HashDigest getLatestBlockHash();

	/**
	 * 最新区块；
	 * 
	 * @return
	 */
	LedgerBlock getLatestBlock();

	/**
	 * 指定高度的区块哈希；
	 * 
	 * @param height
	 * @return
	 */
	HashDigest getBlockHash(long height);

	/**
	 * 指定高度的区块；
	 * 
	 * @param height
	 * @return
	 */
	LedgerBlock getBlock(long height);

	LedgerAdminInfo getAdminInfo();
	
	LedgerAdminInfo getAdminInfo(LedgerBlock block);

	LedgerBlock getBlock(HashDigest hash);

	LedgerDataQuery getDataSet(LedgerBlock block);

	TransactionSet getTransactionSet(LedgerBlock block);

	UserAccountQuery getUserAccountSet(LedgerBlock block);

	DataAccountQuery getDataAccountSet(LedgerBlock block);

	ContractAccountQuery getContractAccountSet(LedgerBlock block);

	default LedgerDataQuery getDataSet() {
		return getDataSet(getLatestBlock());
	}

	default TransactionSet getTransactionSet() {
		return getTransactionSet(getLatestBlock());
	}

	default UserAccountQuery getUserAccountSet() {
		return getUserAccountSet(getLatestBlock());
	}

	default DataAccountQuery getDataAccountSet() {
		return getDataAccountSet(getLatestBlock());
	}

	default ContractAccountQuery getContractAccountSet() {
		return getContractAccountSet(getLatestBlock());
	}

	/**
	 * 重新检索最新区块，同时更新缓存；
	 * 
	 * @return
	 */
	LedgerBlock retrieveLatestBlock();

	/**
	 * 重新检索最新区块，同时更新缓存；
	 * 
	 * @return
	 */
	long retrieveLatestBlockHeight();

	/**
	 * 重新检索最新区块哈希，同时更新缓存；
	 * 
	 * @return
	 */
	HashDigest retrieveLatestBlockHash();

	/**
	 * 创建新区块的编辑器；
	 * 
	 * @return
	 */
	LedgerEditor createNextBlock();

	/**
	 * 获取新区块的编辑器；
	 * <p>
	 * 
	 * 如果未创建新的区块，或者新区块已经提交或取消，则返回 null;
	 * 
	 * @return
	 */
	LedgerEditor getNextBlockEditor();

	@Override
	void close();
}
