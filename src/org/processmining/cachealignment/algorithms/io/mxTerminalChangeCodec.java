/**
 * Copyright (c) 2006, Gaudenz Alder
 */
package org.processmining.cachealignment.algorithms.io;

import org.processmining.cachealignment.algorithms.model.mxGraphModel;
import org.w3c.dom.Node;

import java.util.Map;

/**
 * Codec for mxChildChanges. This class is created and registered
 * dynamically at load time and used implicitely via mxCodec
 * and the mxCodecRegistry.
 */
public class mxTerminalChangeCodec extends mxObjectCodec
{

	/**
	 * Constructs a new model codec.
	 */
	public mxTerminalChangeCodec()
	{
		this(new mxGraphModel.mxTerminalChange(), new String[] { "model", "previous" },
				new String[] { "cell", "terminal" }, null);
	}

	/**
	 * Constructs a new model codec for the given arguments.
	 */
	public mxTerminalChangeCodec(Object template, String[] exclude,
			String[] idrefs, Map<String, String> mapping)
	{
		super(template, exclude, idrefs, mapping);
	}

	/* (non-Javadoc)
	 * @see org.processmining.io.mxObjectCodec#afterDecode(org.processmining.io.mxCodec, org.w3c.dom.Node, java.lang.Object)
	 */
	@Override
	public Object afterDecode(mxCodec dec, Node node, Object obj)
	{
		if (obj instanceof mxGraphModel.mxTerminalChange)
		{
			mxGraphModel.mxTerminalChange change = (mxGraphModel.mxTerminalChange) obj;

			change.setPrevious(change.getTerminal());
		}

		return obj;
	}

}
