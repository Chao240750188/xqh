package com.essence.business.xqh.common.util;

import java.io.*;

/**
 * 复制文件夹下的所有文件到指定文件夹
 * @author lsh
 * 2017-3-23
 */
public class CopyFile {

	/**
	 * 复制文件夹下的指定文件到指定文件夹
	 * @param path   模板文件路径
	 * @param copyPath   新文件夹路径
	 * @param newFileName  复制后文件的名称（ 不含有后缀）
	 * @throws IOException
	 */
	public static String copy(String path, String copyPath, String newFileName) throws IOException {
		System.out.println("开始复制excel文件");
		System.out.println("模板文件路径:"+path);
		System.out.println("新文件夹路径:"+copyPath);
		System.out.println("复制后文件的名称:"+newFileName);
		File filePath = new File(path); //文件路径
		File newPath = new File(copyPath); //新的路径
		String dirAndFilePath=null; //copy的文件路径
		if ( filePath.exists() && filePath.isFile()){ //如果文件存在
			String[] fileNames = path.split("/");
			String fileName = fileNames[fileNames.length - 1]; //文件名
			String prefix=fileName.substring(fileName.lastIndexOf(".")+1); //文件后缀名

			//若新路径不存在则创建一个路径
			if (!newPath.exists() && !newPath.isDirectory()){
				newPath.mkdirs();
			 }
			String newFilePath=copyPath+"/"+newFileName+"."+prefix; //新文件的路径
			dirAndFilePath=copyPath+"&"+newFileName; //返回的格式为：文件夹路径&文件名  ：E:/thflood_model/THFloodFiles/model/maxLogic/data/&hjh_gridTemplate_60000_1
			File file = new File(newFilePath);
				if (!file.exists() && file.isFile()){  //如果文件不存在，重新创建文件
					file.createNewFile();
				}

				//向新文件中写入内容
				InputStream fis = new FileInputStream(path);      //读入原文件
				FileOutputStream fos = new FileOutputStream(newFilePath);
				byte[] buffer = new byte[1444];
				int byteread=0;
				while ( (byteread = fis.read(buffer)) != -1) {
					fos.write(buffer, 0, byteread);
				}
				fis.close();
				fos.close();
			}
		return dirAndFilePath;
	}


	/**
	 *复制文件夹下的所有文件到指定文件夹
	 * @param path 文件夹路径
	 * @param copyPath  新文件夹路径
	 * //@param newFileName 复制后文件的名称（ 不含有后缀）
	 * @throws IOException
	 */
	public static void  copyShpFiles(String path, String copyPath, Object indexNum) throws IOException {
		File filePath = new File(path);
		DataInputStream read ;
		DataOutputStream write;
		if(filePath.isDirectory()){
			File[] list = filePath.listFiles();
			for(int i=0; i<list.length; i++){
				String name = list[i].getName();
				String[] split = name.split("\\.");
				String prefix; //后缀
				if(split.length>2){
					prefix = split[1]+"."+split[2];
				}else{
					prefix = split[1];
				}
				String newPath = path + "/" + name;

				String newCopyPath=null;
				if (null!=indexNum){
					newCopyPath = copyPath + "/"+split[0]+"_"+indexNum+"."+prefix; //过程数据加上过程编号
				}else{
					newCopyPath = copyPath + "/"+split[0]+"."+prefix; //最大水深数据不需要编号

				}
				copyShpFiles(newPath, newCopyPath,indexNum);
			}
		}else if(filePath.isFile()){
			read = new DataInputStream(
					new BufferedInputStream(new FileInputStream(path)));
			write = new DataOutputStream(
					new BufferedOutputStream(new FileOutputStream(copyPath)));
			byte [] buf = new byte[1024*512];
			while(read.read(buf) != -1){
				write.write(buf);
			}
			read.close();
			write.close();
		}else{
			System.out.println("请输入正确的文件名或路径名");
		}
	}



	/**
	 * 删除文件夹中的当前过程中的文件
	 * @param path  文件夹路径
	 * @param processNum  当前过程数  无后缀
	 */
	public static void  deleteSpecialFile(String path, Integer processNum){

		System.out.println("开始判断文件是否存在。。。");
		File filePath = new File(path);
		if(filePath.exists()&&filePath.isDirectory()) {
			File[] list = filePath.listFiles();
			for (int i = 0; i < list.length; i++) {
				String name = list[i].getName();
				String[] split = name.split("\\.");
				String[] splits = split[0].split("_");
				System.out.println();
				//判断文件是否存在
				if (splits[(splits.length-1)].equals(String.valueOf(processNum))){  //当前过程数的文件
					boolean flag = new File(path + "/" + name).delete();
					if (flag){
						System.out.println("删除文件"+name+"成功!");
					}else {
						System.out.println("删除文件"+name+"失败!");
					}
				}

			}
		}else {
			System.out.println("文件夹不存在");
		}
	}



}
