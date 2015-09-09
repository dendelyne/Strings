package modules.suffixTree.suffixTree.applications;

import java.util.EventListener;

import modules.suffixTree.suffixTree.applications.event.MyEntryEvent;
import modules.suffixTree.suffixTree.applications.event.MyExitEvent;

public interface ITreeWalkerListener extends EventListener {

	void entryaction(MyEntryEvent entryEvent);

	void exitaction(MyExitEvent exitEvent);
}