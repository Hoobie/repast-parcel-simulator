package pl.edu.agh.model.style;

import gov.nasa.worldwind.WorldWind;
import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.render.BasicWWTexture;
import gov.nasa.worldwind.render.Offset;
import gov.nasa.worldwind.render.WWTexture;

import java.net.URL;

import pl.edu.agh.model.ParcelAgent;
import repast.simphony.visualization.gis3D.style.DefaultMarkStyle;

public class ParcelStyle extends DefaultMarkStyle<ParcelAgent>{
	Offset iconOffset = new Offset(0.5d, 0.5d, AVKey.FRACTION, AVKey.FRACTION);
	
	@Override
	public WWTexture getTexture(ParcelAgent agent, WWTexture texture) {
		if (texture != null)
			return texture;
		
		URL localUrl = WorldWind.getDataFileStore().requestFile("icons/parcel.png");
		if (localUrl != null)	{
			return new BasicWWTexture(localUrl, false);
		}
		
		return null;
	}
	
	@Override
	public Offset getIconOffset(ParcelAgent agent){
		return iconOffset;
	}
}
