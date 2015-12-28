package com.sinovate.ngrms.pahdx.svrinter.mapseladdr.aui.app;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.context.annotation.ImportResource;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * 根据用户的输入条件更新索引数据
 * @author zszhang
 * @version 1.0
 * @created 18-五月-2015 13:39:06
 */
@ImportResource(value="classpath:app-context.xml")
public class Updater implements CommandLineRunner {

	private static final Logger logger = LoggerFactory.getLogger(Updater.class);

	private static final ArrayList<String> addrTypes = new ArrayList<String>(Arrays.asList("sugg", "user"));
	private static final ArrayList<String> areaCodes = new ArrayList<String>(
			Arrays.asList("550", "551", "552", "553", "554", "555", "556", "557",
						  "558", "559", "561", "562", "563", "564", "566", "567"));

	@Autowired
	public TotalFtsSync m_TotalFtsSync;

	public Updater(){

	}

	public void finalize() throws Throwable {

	}

	/**
	 * 检查main函数的输入参数是否正确，如果不正确就调用pringUsage()输出该工具的正确使用方法；
	 * 
	 * @param args
	 */
	public static boolean checkArgs(String[] args){
		if (args.length > 2) {
			System.err.println("命令行参数错误(参数不应大于两个），无法更新数据！");
			printUsage();
			return false;
		}
		if (args.length == 1 && !addrTypes.contains(args[0]) && !args[0].equals("grid")) {
			System.err.println("命令行参数错误（addrType错误），无法更新数据！");
			printUsage();
			return false;
		}
		if (args.length == 2 && (!addrTypes.contains(args[0]) || !areaCodes.contains(args[1]))) {
			System.err.println("命令行参数错误（addrType或areaCode错误），无法更新数据！");
			printUsage();
			return false;
		}

		return true;

	}

	/**
	 * 输出使用方法如下：
	 * Usage: java -jar prog.jar [addrType [areaCode]]
	 * BTW: addrType 可以取值为 sugg 或 user 分别代表建议地址和用户地址。
	 */
	private static void printUsage(){
		System.out.println("Usage: java -jar prog.jar [addrType [areaCode]]");
		System.out.println("BTW: addrType 可以取值为 sugg 或 user 分别代表建议地址和用户地址。");
	}

	/**
	 * 手工同步刷新地址索引数据的工具
	 * 1. 检查输入参数，调用checkArgs方法
	 * 2. 根据输入参数的类型调用totalFtsSync中的不同的方法进行同步，如：输入sugg,则调用syncSuggAddr()方法；
	 *            输入user，则调用syncUserAddr()方法；
	 *            输入sugg 551两个参数则调用syncSuggAddr(String)方法;
	 *            输入user 551两个参数则调用synvUserAddr(String)方法;
	 * 
	 * @param args
	 */
	public static void main(String[] args){
		SpringApplication sa = new SpringApplication(Updater.class);
		sa.setShowBanner(false);
		sa.setWebEnvironment(false);
		sa.run(args);
	}

	/**
	 * Callback used to run the bean.
	 *
	 * @param args incoming main method arguments
	 * @throws Exception on error
	 */
	@Override
	public void run(String... args) throws Exception {
		if (checkArgs(args) == false) {
			return;
		}

		if (args.length == 0) {
			m_TotalFtsSync.syncSuggAddr();
			m_TotalFtsSync.syncUserAddr();
			return;
		}

		if (args.length == 1 && args[0].equals("grid")) {
			m_TotalFtsSync.syncGridCharacter();
		}

		if (args.length == 1 && args[0].equals("sugg")) {
			m_TotalFtsSync.syncSuggAddr();
			return;
		}

		if (args.length == 1 && args[0].equals("user")) {
			m_TotalFtsSync.syncUserAddr();
			return;
		}

		if (args.length == 2 && args[0].equals("sugg")) {
			m_TotalFtsSync.syncSuggAddr(args[1]);
			return;
		}

		if (args.length == 2 && args[0].equals("user")) {
			m_TotalFtsSync.syncUserAddr(args[1]);
			Thread.sleep(10000);
			return;
		}
	}
}