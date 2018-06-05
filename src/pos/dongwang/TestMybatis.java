package pos.dongwang;


import org.apache.ibatis.session.SqlSession;
import pos.dongwang.mapper.BranchMapper;
import pos.dongwang.mybatis.MyBatisSqlSessionFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

public class TestMybatis {

	public static void main(String[] args) throws IOException {
		/*SqlSession session = MyBatisSqlSessionFactory.getSqlSessionFactory().openSession();
		BranchMapper branchMapper = session.getMapper(BranchMapper.class);
		String line = branchMapper.getLineByBarcode("125");
		System.out.println(line);
		session.close();
		*/
		Runtime runtime =Runtime.getRuntime(); // 获取当前程序的运行进对象
		Process process = null; //声明处理类对象
		String line = null; //返回行信息
		InputStream is = null; //输入流
		InputStreamReader isr = null;// 字节流
		BufferedReader br = null;
		String ip = "192.168.90.200";
		boolean res = false;// 结果
		try {
			process =runtime.exec("ping " + ip); // PING
			is =process.getInputStream(); // 实例化输入流
			isr = new InputStreamReader(is);// 把输入流转换成字节流
			br = new BufferedReader(isr);// 从字节中读取文本
			while ((line= br.readLine()) != null) {
				if(line.contains("TTL")) {
					res= true;
					break;
				}
			}
			is.close();
			isr.close();
			br.close();
			if (res){
				System.out.println("ping通  ...");
			} else{
				System.out.println("ping不通...");
			}
		} catch (IOException e) {
			System.out.println(e);
			runtime.exit(1);
		}
}

}