package actions.comparators.imagemagik;

import actions.comparators.ImageComparator;
import org.apache.log4j.Logger;
import org.im4java.core.CompareCmd;
import org.im4java.core.IM4JavaException;
import org.im4java.core.IMOperation;

import java.io.File;
import java.io.IOException;

/**
 * Created by asher.saban on 6/8/2015.
 */
public class ImageMagikComparator implements ImageComparator {

	private static final Logger log = Logger.getLogger(ImageMagikComparator.class);
	private double precision;
	private String diffImagePath;

	public ImageMagikComparator(double precision,String diffImagePath) {
		this.precision = precision;
		this.diffImagePath = diffImagePath;
	}

	@Override
	public boolean isSimilar(File image1, File image2) {
		double diff = compareImages(image1.getAbsolutePath(), image2.getAbsolutePath());
//		log.info("diff: " + diff);
		return (diff <= precision);
	}

	private double compareImages(String srcImagePath, String destImagePath) {

		CompareCmd compare = new CompareCmd();

		// The CLI output of compare command is returned as "error"
		KMagicComparisonOutputConsumer consumer = new KMagicComparisonOutputConsumer();
		compare.setErrorConsumer(consumer);

		IMOperation cmpOp = createIMOperation();
		runIMOperation(srcImagePath, destImagePath, compare, cmpOp);
		return consumer.getDifferenceScore();
	}

	private void runIMOperation(String srcImagePath, String destImagePath, CompareCmd compare, IMOperation cmpOp) {
		try {
			compare.run(cmpOp, srcImagePath, destImagePath, diffImagePath); // We can show a diff image if we want
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (IM4JavaException e) {
//			e.printStackTrace();
			// Nothing to do
		}
	}

	private IMOperation createIMOperation() {
		IMOperation cmpOp = new IMOperation();
		cmpOp.addImage();
		cmpOp.addImage();
		cmpOp.metric("PHASH");
		cmpOp.addImage();
		return cmpOp;
	}
}