package edu.southwestern.util.sound;

/*
A simple Java class that writes a MIDI file

(c)2011 Kevin Boone, all rights reserved
 */
import java.io.*;
import java.util.*;


public class MIDIExperiment
{
	// Note lengths
	//  We are working with 32 ticks to the crotchet. So
	//  all the other note lengths can be derived from this
	//  basic figure. Note that the longest note we can
	//  represent with this code is one tick short of a 
	//  two semibreves (i.e., 8 crotchets)

	static final int SEMIQUAVER = 4;
	static final int QUAVER = 8;
	static final int CROTCHET = 16;
	static final int MINIM = 32;
	static final int SEMIBREVE = 64;

	// Standard MIDI file header, for one-track file
	// 4D, 54... are just magic numbers to identify the
	//  headers
	// Note that because we're only writing one track, we
	//  can for simplicity combine the file and track headers
	static final int header[] = new int[]
			{
					0x4d, 0x54, 0x68, 0x64, 0x00, 0x00, 0x00, 0x06,
					0x00, 0x00, // single-track format
					0x00, 0x01, // one track
					0x00, 0x10, // 16 ticks per quarter
					0x4d, 0x54, 0x72, 0x6B
			};

	// Standard footer
	static final int footer[] = new int[]
			{
					0x01, 0xFF, 0x2F, 0x00
			};

	// A MIDI event to set the tempo
	static final int tempoEvent[] = new int[]
			{
					0x00, 0xFF, 0x51, 0x03, 
					0x0F, 0x42, 0x40 // Default 1 million usec per crotchet
			};

	// A MIDI event to set the key signature. This is irrelent to
	//  playback, but necessary for editing applications 
	static final int keySigEvent[] = new int[]
			{
					0x00, 0xFF, 0x59, 0x02,
					0x00, // C
					0x00  // major
			};


	// A MIDI event to set the time signature. This is irrelent to
	//  playback, but necessary for editing applications 
	static final int timeSigEvent[] = new int[]
			{
					0x00, 0xFF, 0x58, 0x04,
					0x04, // numerator
					0x02, // denominator (2==4, because it's a power of 2)
					0x30, // ticks per click (not used)
					0x08  // 32nd notes per crotchet 
			};

	// The collection of events to play, in time order
	protected Vector<int[]> playEvents;   

	/** Construct a new MidiFile with an empty playback event list 
	 * @return */
	public void MIDIExample()
	{
		playEvents = new Vector<int[]>();
	}


	/** Write the stored MIDI events to a file */
	public void writeToFile (String filename)
			throws IOException
	{
		FileOutputStream fos = new FileOutputStream (filename);


		fos.write (intArrayToByteArray (header));

		// Calculate the amount of track data
		// _Do_ include the footer but _do not_ include the 
		// track header

		int size = tempoEvent.length + keySigEvent.length + timeSigEvent.length
				+ footer.length;

		for (int i = 0; i < playEvents.size(); i++)
			size += playEvents.elementAt(i).length;

		// Write out the track data size in big-endian format
		// Note that this math is only valid for up to 64k of data
		//  (but that's a lot of notes) 
		int high = size / 256;
		int low = size - (high * 256);
		fos.write ((byte) 0);
		fos.write ((byte) 0);
		fos.write ((byte) high);
		fos.write ((byte) low);


		// Write the standard metadata -- tempo, etc
		// At present, tempo is stuck at crotchet=60 
		fos.write (intArrayToByteArray (tempoEvent));
		fos.write (intArrayToByteArray (keySigEvent));
		fos.write (intArrayToByteArray (timeSigEvent));

		// Write out the note, etc., events
		for (int i = 0; i < playEvents.size(); i++)
		{
			fos.write (intArrayToByteArray (playEvents.elementAt(i)));
		}

		// Write the footer and close
		fos.write (intArrayToByteArray (footer));
		fos.close();
	}


	/** Convert an array of integers which are assumed to contain
    unsigned bytes into an array of bytes */ 
	protected static byte[] intArrayToByteArray (int[] ints)
	{
		int l = ints.length;
		byte[] out = new byte[ints.length]; 
		for (int i = 0; i < l; i++) 
		{
			out[i] = (byte) ints[i];
		}
		return out;
	}


	/** Store a note-on event */
	public void noteOn (int delta, int note, int velocity)
	{
		int[] data = new int[4];
		data[0] = delta;
		data[1] = 0x90;
		data[2] = note;
		data[3] = velocity;
		playEvents.add (data);
	}


	/** Store a note-off event */
	public void noteOff (int delta, int note)
	{
		int[] data = new int[4];
		data[0] = delta;
		data[1] = 0x80;
		data[2] = note;
		data[3] = 0;
		playEvents.add (data);
	}


	/** Store a program-change event at current position */
	public void progChange (int prog)
	{
		int[] data = new int[3];
		data[0] = 0;
		data[1] = 0xC0;
		data[2] = prog;
		playEvents.add (data);
	}


	/** Store a note-on event followed by a note-off event a note length
    later. There is no delta value -- the note is assumed to
    follow the previous one with no gap. */
	public void noteOnOffNow (int duration, int note, int velocity)
	{
		noteOn (0, note, velocity);
		noteOff (duration, note);
	}


	public void noteSequenceFixedVelocity (int[] sequence, int velocity)
	{
		boolean lastWasRest = false;
		int restDelta = 0;
		for (int i = 0; i < sequence.length; i += 2)
		{
			int note = sequence[i];
			int duration = sequence[i + 1];
			if (note < 0) 
			{
				// This is a rest
				restDelta += duration;
				lastWasRest = true;
			} 
			else
			{
				// A note, not a rest
				if (lastWasRest)
				{
					noteOn (restDelta, note, velocity);
					noteOff (duration, note);
				}
				else
				{
					noteOn (0, note, velocity);
					noteOff (duration, note);
				}
				restDelta = 0;
				lastWasRest = false;
			}
		}
	}


	/** Test method -- creates a file test1.mid when the class
    is executed */ 
//	public static void main (String[] args)
//			throws Exception
//	{
//		MidiFile mf = new MidiFile();
//
//		// Test 1 -- play a C major chord
//
//		// Turn on all three notes at start-of-track (delta=0) 
//		mf.noteOn (0, 60, 127);
//		mf.noteOn (0, 64, 127);
//		mf.noteOn (0, 67, 127);
//
//		// Turn off all three notes after one minim. 
//		// NOTE delta value is cumulative -- only _one_ of
//		//  these note-offs has a non-zero delta. The second and
//		//  third events are relative to the first
//		mf.noteOff (MINIM, 60);
//		mf.noteOff (0, 64);
//		mf.noteOff (0, 67);
//
//		// Test 2 -- play a scale using noteOnOffNow
//		//  We don't need any delta values here, so long as one
//		//  note comes straight after the previous one 
//
//		mf.noteOnOffNow (QUAVER, 60, 127);
//		mf.noteOnOffNow (QUAVER, 62, 127);
//		mf.noteOnOffNow (QUAVER, 64, 127);
//		mf.noteOnOffNow (QUAVER, 65, 127);
//		mf.noteOnOffNow (QUAVER, 67, 127);
//		mf.noteOnOffNow (QUAVER, 69, 127);
//		mf.noteOnOffNow (QUAVER, 71, 127);
//		mf.noteOnOffNow (QUAVER, 72, 127);
//
//		// Test 3 -- play a short tune using noteSequenceFixedVelocity
//		//  Note the rest inserted with a note value of -1
//
//		int[] sequence = new int[]
//				{
//						60, QUAVER + SEMIQUAVER,
//						65, SEMIQUAVER,
//						70, CROTCHET + QUAVER,
//						69, QUAVER,
//						65, QUAVER / 3,
//						62, QUAVER / 3,
//						67, QUAVER / 3,
//						72, MINIM + QUAVER,
//						-1, SEMIQUAVER,
//						72, SEMIQUAVER,
//						76, MINIM,
//				};
//
//		// What the heck -- use a different instrument for a change
//		mf.progChange (10);
//
//		mf.noteSequenceFixedVelocity (sequence, 127);
//
//		mf.writeToFile ("test1.mid");
//	}
}
