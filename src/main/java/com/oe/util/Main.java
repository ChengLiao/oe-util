package com.oe.util;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.log4j.Logger;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.oe.util.service.TransferService;


/**
 * @author 928
 * @date 2016-8-9
 */
public class Main {
	public static enum COMMAND {
		/**
		 * 元数据
		 */
		mds,
		/**
		 * 元数据
		 */
		dqc,
		/**
		 * 元数据
		 */
		portal,
		/**
		 * 用户
		 */
		user,
		/**
		 * 分页数据
		 */
		pagination,
		/**
		 * 版本信息
		 */
		version
	}
	private static Logger logger = Logger.getLogger(Main.class);
	public static void main(String[] args) {
		int status = 0;
		long start = System.currentTimeMillis();
		Options options = buildOptions();
		CommandLineParser parser = new DefaultParser();
		CommandLine cli = null;
		
		try {
//			handle(COMMAND.dqc);
			cli = parser.parse(options, args);
			if (cli.hasOption(COMMAND.version.name())) {
				printVersion();
			} else if (cli.hasOption(COMMAND.mds.name())) {
				handle(COMMAND.mds);
			} else if (cli.hasOption(COMMAND.dqc.name())) {
				handle(COMMAND.dqc);
			} else if (cli.hasOption(COMMAND.portal.name())) {
				handle(COMMAND.portal);
			} else if (cli.hasOption(COMMAND.user.name())) {
				handle(COMMAND.user);
			} else if (cli.hasOption(COMMAND.pagination.name())) {
				handle(COMMAND.pagination);
			} else {
				printHelp(options);
			}
		} catch (Throwable e) {
			logger.error(e , e);
			status = -1;
		}
		long end = System.currentTimeMillis();
		logger.fatal(String.format("transfer end,%s", new Date()));
		logger.info(Util.calculateTimeElapsed(start, end));
		System.exit(status);
	}
	
	
	private static void handle(COMMAND cmd) throws Throwable {
		ConfigurableApplicationContext context = null;
		long start = System.currentTimeMillis();
		logger.info("Initializing Spring context.");
		try {
			System.out.println(cmd.name());
			context = new ClassPathXmlApplicationContext(
					"/util-context.xml");
			TransferService service = (TransferService) context.getBean(String.format("%sTransferService", cmd.name()));
			logger.info("start transfer...");
			service.transfer();
			logger.info("transfer complete...");
		}finally{
			if (context != null) {
				logger.info("Closing Spring context.");
				context.close();
			}
			long end = System.currentTimeMillis();
			logger.info(String.format("loader parser end,%s,%s", new Date(), Util.calculateTimeElapsed(start, end)));
		}
	}
	
	
	/**
	 * 构建命令行参数
	 * 
	 * @return
	 */
	private static Options buildOptions() {
		Options options = new Options();
		options.addOption(
				COMMAND.version.name().substring(0, 1),
				COMMAND.version.name(),
				false,
				"print version.");
		
		
		options.addOption(
				COMMAND.mds.name().substring(0, 1),
				COMMAND.mds.name(),
				false,
				"transfer mds .");
		
		
		options.addOption(
				COMMAND.dqc.name().substring(0, 1),
				COMMAND.dqc.name(),
				false,
				"transfer dqc .");
		
		options.addOption(
				COMMAND.portal.name().substring(0, 1),
				COMMAND.portal.name(),
				false,
				"transfer portal .");
		
		options.addOption(
				COMMAND.user.name().substring(0, 1),
				COMMAND.user.name(),
				false,
				"transfer user password.");
		
		options.addOption(
				COMMAND.pagination.name().substring(0, 1),
				COMMAND.pagination.name(),
				false,
				"transfer pagination.");
		
		return options;
	}
	
	private static void printHelp(Options options) {
		HelpFormatter hf = new HelpFormatter();
		int width = 80;
		String cmdLineSyntax = "run";
		String header = "args:";
		String footer = "";
		hf.printHelp(width, cmdLineSyntax, header, options, footer,true);
	}
	
	private static void printVersion() {
		String program = "",release="",build="";
		File lib = new File("./lib");
		File[] jarFiles = lib.listFiles(new FilenameFilter() {
			
			@Override
			public boolean accept(File dir, String name) {
				return name.startsWith("mds-util");
			}
		});
		if(jarFiles != null){
			for(File jarFile : jarFiles){
				try {
					Manifest mf = new JarFile(jarFile).getManifest();
					program = "PROGRAM: " + mf.getMainAttributes().getValue("Implementation-Title");
					release = "RELEASE: " + mf.getMainAttributes().getValue("Implementation-Version");
					build = "BUILD:   " + mf.getMainAttributes().getValue("Build-Time");
				} catch (IOException e) {
				}
			}
		}
		List<String> infos = new ArrayList<String>();
		infos.add(" Copyright 2013-2017, Teradata Corporation.");
		infos.add(" All Rights Reserved.");
		infos.add("");
		infos.add(String.format(" *   *  ****    ****"));
		infos.add(String.format(" ** **  *   *  *     %s", program));
		infos.add(String.format(" * * *  *   *   ***  %s", release));
		infos.add(String.format(" *   *  *   *      * %s", build));
		infos.add(String.format(" *   *  ****   ****  %s", build));
		infos.add(String.format(""));
		infos.add("");

		for(String info:infos){
			if(logger.getAllAppenders().hasMoreElements()){
				logger.info(info);
			}else{
				System.out.println(info);
			}
		}
	}
}
