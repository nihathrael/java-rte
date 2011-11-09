package rte.pairs;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class AdvPair {

	public int id;
	public String task;
	public boolean entailment;

	Text text;
	Text hypothesis;

	public static AdvPair fromXML(Element pairElement) {
		AdvPair pair = new AdvPair();
		pair.id = Integer.parseInt(pairElement.getAttribute("id"));
		pair.task = pairElement.getAttribute("task");
		pair.entailment = pairElement.getAttribute("entailment").equals("yes");
		
		Element text = (Element)pairElement.getElementsByTagName("text").item(0);
		pair.text = Text.fromXML(text);
		
		Element hypothesis = (Element)pairElement.getElementsByTagName("hypothesis").item(0);
		pair.hypothesis = Text.fromXML(hypothesis);
		

		return pair;
	}

}
