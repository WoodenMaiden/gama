/*******************************************************************************************************
 *
 * ImageConstantSupplier.java, in gama.extension.image, is part of the source code of the GAMA modeling and
 * simulation platform .
 *
 * (c) 2007-2024 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package gama.extension.image;

import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import gama.gaml.constants.IConstantAcceptor;
import gama.gaml.constants.IConstantsSupplier;

/**
 * The Class ImageConstantSupplier.
 */
public class ImageConstantSupplier implements IConstantsSupplier {

	@Override
	public void supplyConstantsTo(final IConstantAcceptor acceptor) {
		InputStream is = getClass().getResourceAsStream("icons/gama.png");
		GamaImage im;
		try {
			im = GamaImage.from(ImageIO.read(is), true);
			acceptor.accept("gama_logo", im, "The official logo of GAMA in a 500x500 image", null, false);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
