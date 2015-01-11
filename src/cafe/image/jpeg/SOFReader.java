/**
 * Copyright (c) 2014 by Wen Yu.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Any modifications to this file must keep this entire header intact.
 */

package cafe.image.jpeg;

import java.io.IOException;
import java.util.EnumSet;

import cafe.io.IOUtils;
import cafe.util.Reader;

/**
 * JPEG SOF segment reader
 *  
 * @author Wen Yu, yuwen_66@yahoo.com
 * @version 1.0 10/09/2013
 */
public class SOFReader implements Reader {

	private int precision;
	private int frameHeight;
	private int frameWidth;
	private int numOfComponents;
	private Component[] components;
	private static final EnumSet<Marker> SOFS = 
			EnumSet.of(Marker.SOF0, Marker.SOF1, Marker.SOF2, Marker.SOF3, Marker.SOF5, 
		               Marker.SOF6, Marker.SOF7, Marker.SOF9, Marker.SOF10, Marker.SOF11,
		               Marker.SOF13, Marker.SOF14, Marker.SOF15)
	        ;
	
	private Segment segment;
	
	public SOFReader(Segment segment) throws IOException {
		//
		if(!SOFS.contains(segment.getMarker())) {
			throw new IllegalArgumentException("Not a valid SOF segment: " + segment.getMarker());
		}
		
		this.segment = segment;
		read();
	}
	
	public int getLength() {
		return segment.getLength();
	}
	
	public int getPrecision() {
		return precision;
	}
	
	public int getFrameHeight() {
		return frameHeight;
	}
	
	public int getFrameWidth() {
		return frameWidth;
	}
	
	public int getNumOfComponents() {
		return numOfComponents;
	}
	
	public Component[] getComponents() {
		return components.clone();
	}
	
	@Override
	public void read() throws IOException {
		//
		byte[] data = segment.getData();
		precision = data[0];
		frameHeight = IOUtils.readUnsignedShortMM(data, 1);
		frameWidth = IOUtils.readUnsignedShortMM(data, 3);
		
		numOfComponents = data[5];
		components = new Component[numOfComponents];
	
		int offset = 6;
		
		for (int i = 0; i < numOfComponents; i++) {
			byte componentId = data[offset++];		
			byte sampleFactor = data[offset++];
			byte hSampleFactor = (byte)((sampleFactor>>4)&0x0f);
			byte vSampleFactor = (byte)((sampleFactor&0x0f));
			byte qTableNumber = data[offset++];
					
			components[i] = new Component(componentId, hSampleFactor, vSampleFactor, qTableNumber);
		}
	}
}