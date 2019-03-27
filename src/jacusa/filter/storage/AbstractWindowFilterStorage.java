package jacusa.filter.storage;

import java.util.Arrays;

import jacusa.cli.parameters.AbstractParameters;
import jacusa.cli.parameters.SampleParameters;

import jacusa.pileup.builder.WindowCache;
import jacusa.util.WindowCoordinates;

import net.sf.samtools.SAMRecord;

public abstract class AbstractWindowFilterStorage extends AbstractFilterStorage<WindowCache> {

	// count indel, read start/end, splice site as only 1!!!
	// this ensure that a base-call will only be counted once...
	private boolean[] visited;
	protected int windowSize;
	protected WindowCache windowCache;

	private SampleParameters sampleParameters;
	
	// container for current SAMrecord
	protected SAMRecord record;

	public AbstractWindowFilterStorage(final char c, 
			final WindowCoordinates windowCoordinates, 
			final SampleParameters sampleParameters, 
			final AbstractParameters parameters) {
		super(c);

		windowSize = parameters.getWindowSize();
		visited = new boolean[windowSize];
		
		final int baseLength = parameters.getBaseConfig().getBaseLength();
		setContainer(new WindowCache(windowCoordinates, baseLength));
		windowCache = getContainer();
		
		this.sampleParameters = sampleParameters;
	}

	protected void addRegion(int genomicPosition, int length, int readPosition, int[] byte2int, SAMRecord record) {
		if (this.record != record) {
			this.record = record;
			Arrays.fill(visited, false);
		}

		int windowPosition = windowCache.getWindowCoordinates().get2WindowPosition(genomicPosition);
		
		int offset = 0;

		if (readPosition < 0) {
			offset += Math.abs(readPosition);
			
			windowPosition += offset;
			readPosition += offset;
			length -= offset;
		}

		if (windowPosition < 0) {
			offset += Math.abs(windowPosition);
			
			windowPosition += offset;
			readPosition += offset;
			length -= offset;
		}

		for (int i = 0; i < length && windowPosition + i < windowSize && readPosition + i < record.getReadLength(); ++i) {
			if (! visited[windowPosition + i]) {
				final int baseI = byte2int[record.getReadBases()[readPosition + i]];

				// corresponds to N -> ignore
				if (baseI < 0) {
					continue;
				}

				byte qual = record.getBaseQualities()[readPosition + i];
				// int genomicPosition = windowCache.getWindowCoordinates().getGenomicPosition(windowPosition + i);
				if (qual >= sampleParameters.getMinBASQ()) {
					windowCache.addHighQualityBaseCall(windowPosition + i, baseI, qual);
					visited[windowPosition + i] = true;
				}
			}
		}
	}

	@Override
	public void clearContainer() {
		getContainer().clear();
	}

}