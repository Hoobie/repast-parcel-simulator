package pl.edu.agh.model.style;

import gov.nasa.worldwind.WorldWind;
import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.render.BasicWWTexture;
import gov.nasa.worldwind.render.Offset;
import gov.nasa.worldwind.render.WWTexture;

import java.net.URL;

import pl.edu.agh.model.ParcelMachineAgent;
import repast.simphony.visualization.gis3D.style.DefaultMarkStyle;

public class ParcelMachineStyle extends DefaultMarkStyle<ParcelMachineAgent>{
	Offset iconOffset = new Offset(0.5d, 0.5d, AVKey.FRACTION, AVKey.FRACTION);
	
	@Override
	public WWTexture getTexture(ParcelMachineAgent agent, WWTexture texture) {
		if (texture != null)
			return texture;
		
		URL localUrl = WorldWind.getDataFileStore().requestFile("icons/parcel-machine.png");
		if (localUrl != null)	{
			return new BasicWWTexture(localUrl, false);
		}
		
		return null;
	}
	
	@Override
	public Offset getIconOffset(ParcelMachineAgent agent){
		return iconOffset;
	}
}
