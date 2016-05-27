package cz.cuni.amis.pogamut.ut2004.utils;

import cz.cuni.amis.pogamut.ut2004.observer.exception.UCCStartException;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class MyUCCWrapper extends UCCWrapper {

	public MyUCCWrapper(UCCWrapperConf configuration) throws UCCStartException {
		super(configuration);
	}

	@Override
	protected void initUCCWrapper() throws UCCStartException {

		boolean exception = false;
		try {
			// start new ucc instance
			String id = System.currentTimeMillis() + "a" + fileCounter++;
			String fileWithPorts = "GBports" + id;
			String uccHomePath = getUnrealHome();
			String systemDirPath = uccHomePath + File.separator + "System" + File.separator;

			// default ucc executable for Windows
			String uccFile = "ucc.exe";

			// determine OS type, if it isn't win then add option to ucc
			String options = "";
			if (!System.getProperty("os.name").contains("Windows")) {
				options = " -nohomedir";
				uccFile = "ucc";
				if (System.getProperty("os.name").toLowerCase().contains("linux")) {
					uccFile = "ucc-bin";
					if (System.getProperty("os.arch").toLowerCase().contains("amd64")) {
						Logger.getLogger("UCCWrapper").info(
								"64bit arch detected (os.arch property contains keyword amd64). Using 64bit binarry.");
						uccFile += "-linux-amd64";
					}
				}
			}

			MyUCCWrapperConf conf = (MyUCCWrapperConf) this.configuration;

			String execStr = systemDirPath + uccFile;
			String portsSetting = conf.startOnUnusedPort ? "?PortsLog=" + fileWithPorts + "?bRandomPorts=true" : "";
			String playerPortSetting = conf.playerPort != -1 ? "-port=" + conf.playerPort : "";

			String parameter = conf.mapName + "?game=" + conf.gameBotsPack + "." + conf.gameType + portsSetting
					+ conf.options + options; // + playerPortSetting;

			ProcessBuilder procBuilder = new ProcessBuilder(execStr, "server", parameter, playerPortSetting);
			System.out.println(procBuilder.command());
			procBuilder.directory(new File(systemDirPath));

			uccProcess = procBuilder.start();
			ScannerSink scanner = new ScannerSink(uccProcess.getInputStream());
			scanner.start();
			new StreamSink(uccProcess.getErrorStream()).start();

			scanner.portsBindedLatch.await(3, TimeUnit.MINUTES);
			if (scanner.exception != null) {
				// ucc failed to start
				try {
					uccProcess.destroy();
				} catch (Exception e) {
				}
				uccProcess = null;
				throw scanner.exception;
			}
			if (scanner.portsBindedLatch.getCount() > 0) {
				scanner.interrupt();
				try {
					uccProcess.destroy();
				} catch (Exception e) {
				}
				uccProcess = null;
				throw new UCCStartException("UCC did not start in 3 minutes, timeout.", this);
			}

			controlPort = scanner.controlPort;
			gbPort = scanner.botsPort;
		} catch (InterruptedException ex1) {
			exception = true;
			throw new UCCStartException("Interrupted.", ex1);
		} catch (IOException ex2) {
			exception = true;
			throw new UCCStartException("IO Exception.", ex2);
		} catch (UCCStartException ex3) {
			exception = true;
			throw ex3;
		} catch (Exception ex3) {
			exception = true;
			throw new UCCStartException("Exception.", ex3);
		} finally {
			if (exception) {
				try {
					stop();
				} catch (Exception e) {
				}
			}
		}
	}
}
