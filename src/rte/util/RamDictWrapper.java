package rte.util;

import java.io.IOException;
import java.net.URL;
import java.util.Iterator;

import jsl.measure.jwi.JiangConrathSimilarity;
import jsl.measure.jwi.LinSimilarity;

import edu.mit.jwi.CachingDictionary;
import edu.mit.jwi.Dictionary;
import edu.mit.jwi.IDictionary;
import edu.mit.jwi.RAMDictionary;
import edu.mit.jwi.item.IExceptionEntry;
import edu.mit.jwi.item.IExceptionEntryID;
import edu.mit.jwi.item.IIndexWord;
import edu.mit.jwi.item.IIndexWordID;
import edu.mit.jwi.item.ISenseEntry;
import edu.mit.jwi.item.ISenseKey;
import edu.mit.jwi.item.ISynset;
import edu.mit.jwi.item.ISynsetID;
import edu.mit.jwi.item.IVersion;
import edu.mit.jwi.item.IWord;
import edu.mit.jwi.item.IWordID;
import edu.mit.jwi.item.POS;

/**
 * This class is needed to be able to use the {@link JiangConrathSimilarity} and
 * {@link LinSimilarity} with a {@link RAMDictionary}. This is not natively
 * provided, thus we have to mask it as a {@link Dictionary}.
 * 
 */
public class RamDictWrapper extends Dictionary {

	private IDictionary internalDict;

	public RamDictWrapper(URL file) {
		super(file);
		internalDict = new CachingDictionary(new RAMDictionary(file,
				RAMDictionary.BACKGROUND_LOAD));
	}

	public IVersion getVersion() {
		return internalDict.getVersion();
	}

	public boolean isOpen() {
		return internalDict.isOpen();
	}

	public boolean open() throws IOException {
		return internalDict.open();
	}

	public void close() {
		internalDict.close();
	}

	public IExceptionEntry getExceptionEntry(IExceptionEntryID arg0) {
		return internalDict.getExceptionEntry(arg0);
	}

	public IExceptionEntry getExceptionEntry(String arg0, POS arg1) {
		return internalDict.getExceptionEntry(arg0, arg1);
	}

	public Iterator<IExceptionEntry> getExceptionEntryIterator(POS arg0) {
		return internalDict.getExceptionEntryIterator(arg0);
	}

	public IIndexWord getIndexWord(IIndexWordID arg0) {
		return internalDict.getIndexWord(arg0);
	}

	public IIndexWord getIndexWord(String arg0, POS arg1) {
		return internalDict.getIndexWord(arg0, arg1);
	}

	public Iterator<IIndexWord> getIndexWordIterator(POS arg0) {
		return internalDict.getIndexWordIterator(arg0);
	}

	public ISenseEntry getSenseEntry(ISenseKey arg0) {
		return internalDict.getSenseEntry(arg0);
	}

	public Iterator<ISenseEntry> getSenseEntryIterator() {
		return internalDict.getSenseEntryIterator();
	}

	public ISynset getSynset(ISynsetID arg0) {
		return internalDict.getSynset(arg0);
	}

	public Iterator<ISynset> getSynsetIterator(POS arg0) {
		return internalDict.getSynsetIterator(arg0);
	}

	public IWord getWord(IWordID arg0) {
		return internalDict.getWord(arg0);
	}

	public IWord getWord(ISenseKey arg0) {
		return internalDict.getWord(arg0);
	}

}
