package cn.bpnn;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import javax.sound.sampled.LineListener;
/**
 * 
 * @author Mesie
 *
 * 2017年6月20日
 */
public class Test {
	
	/**
	 * 读取文件
	 * @param filePath
	 * @return
	 */
	public static List<String> readTxtFile(String filePath) {
		try {
			List<String> list=new ArrayList<String>();
			String encoding = "UTF-8";
			File file = new File(filePath);
			// 判断文件是否存在
			if (file.isFile() && file.exists()) { 
				InputStreamReader read = new InputStreamReader(new FileInputStream(file), encoding);// 考虑到编码格式
				BufferedReader bufferedReader = new BufferedReader(read);
				String lineTxt = null;
				
				while ((lineTxt = bufferedReader.readLine()) != null) {
					list.add(lineTxt);
//					System.out.println(lineTxt);					
				}
				read.close();
			} else {
				System.out.println("找不到指定的文件");
			}
			return list;
		} catch (Exception e) {
			System.out.println("读取文件内容出错");
			e.printStackTrace();
		}
		return null;
	}
	
	private static int getRealIndexArray(List<Double> allPriceDoubles, double d) {
		int count=0;
		for (int i = 0; i < allPriceDoubles.size(); i++) {
			if(allPriceDoubles.get(i)<d){
				count++;
			}else {
				break;
			}
		}
		return count;
	}

	private static List<Double> getScore(double d, double e, double i) {
		List<Double> allPriceDoubles=new ArrayList<Double>();
		do {
			d=d+i;
			allPriceDoubles.add(d);
		} while (d<e);
		return allPriceDoubles;
	}
	
	public static void main(String[] args) {
		Test test = new Test();
		List<String> trainData = test.readTxtFile("data/train.txt");
		List<String> testData = test.readTxtFile("data/test.txt");
		
		List<Double> allScoreDoubles=getScore(200.0,1000.0,1.0);
		BPNN bp = new BPNN(24, 23, allScoreDoubles.size());
		int len = 26;
		//开始训练 i迭代次数
		for (int i = 0; i != 5000; i++) {
			for(String data : trainData){
				String[] array=data.split(",");	
				
				double[] real = new double[allScoreDoubles.size()];
				int index=getRealIndexArray(allScoreDoubles,Double.parseDouble(array[len-1]));
				real[index]=1;
				
				double[] binary = new double[len-2];
				for(int j =0;j<len-2;j++){
					if("".equals(array[j+1])){
						binary[j] = 0.0;
					}else{
						binary[j]=Double.parseDouble(array[j+1]);
					}
				}
				if(Double.parseDouble(array[len-1])>0){
					bp.train(binary, real);
				}
			}
		}
			
		System.out.println("----------训练完毕，下面请输入一组数字，神经网络将自动预测判断。--------------");
		
		for(String data : testData){
			String[] array=data.split(",");	
			String comName = array[0];
			double comScore = Double.parseDouble(array[len-1]);
			
			double[] binary = new double[len-2];
			for(int j =0;j<len-2;j++){
				if("".equals(array[j+1])){
					binary[j] = 0.0;
				}else{
					binary[j]=Double.parseDouble(array[j+1]);
				}
			}
			double[] result = bp.test(binary);
			double max = -Integer.MIN_VALUE;
			int idx = -1;
			for (int i = 0; i != result.length; i++) {
//				System.out.println(result[i]);
				if (result[i] > max) {
					max = result[i];
					idx = i;
				}
			}
			System.out.println(comName + ",实际得分："+ comScore + ",预测得分："+allScoreDoubles.get(idx)+"\n");
		}
	}
}
