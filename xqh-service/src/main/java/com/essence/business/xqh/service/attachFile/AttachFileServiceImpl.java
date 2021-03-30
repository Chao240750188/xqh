package com.essence.business.xqh.service.attachFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.zip.ZipOutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.essence.business.xqh.api.attachFile.AttachFileService;
import com.essence.business.xqh.common.util.DateUtil;
import com.essence.business.xqh.common.util.FileUtil;
import com.essence.business.xqh.common.util.PropertiesUtil;
import com.essence.business.xqh.dao.attachFile.AttachFileDao;
import com.essence.business.xqh.dao.entity.attachFile.AttachFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.essence.framework.util.StrUtil;

/**
 * 附件服务
 * 
 * @author NoBugNoCode
 *
 *         2020年4月9日 下午3:44:11
 */
@Service("attachFileService")
@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
public class AttachFileServiceImpl implements AttachFileService {
	@Autowired
	AttachFileDao fileDao;
	//查询附件信息
    Properties prop = PropertiesUtil.read("/filePath.properties");
	private  String  basepath = PropertiesUtil.readKeyValue(prop, "FileSavePath");
	private  String previewBasepath = basepath + "_previewFiles" + File.separator;

	/**
	 * 不要记日志，子方法会记录
	 */
	@Override
	public List<AttachFile> uploadFile(String groupid, MultipartFile[] files) {
		File group = new File(basepath + groupid);
		if (!group.exists()) {
			group.mkdir();
		}
		File[] finalFiles = new File[files.length];
		for (int i = 0; i < files.length; i++) {
			MultipartFile file = files[i];
			String filename = FileUtil.getNewFileName(basepath + groupid, file.getOriginalFilename());
			File newFile = null;
			try {
				newFile = FileUtil.getFile(file.getInputStream(), group.getAbsolutePath() + File.separator + filename);
			} catch (IOException e) {
				newFile = null;
			}
			finalFiles[i] = newFile;
		}
		return uploadFile(groupid, finalFiles);
	}

	@Override
	@Transactional
	public List<AttachFile> updateByGroup(String groupId, MultipartFile[] files) {
		// 删除文件对象数据库数据 和 文件
		deleteFileByGroupId(groupId);
		return uploadFile(groupId, files);
	}

	@Override
	public List<AttachFile> uploadFile(String groupid, File[] files) {
		// 获取当前登陆用户
		// UserInfo userInfo = URMSUtil.getCurrentUserInfo();
		List<AttachFile> list = new ArrayList<AttachFile>();
		File group = new File(basepath + groupid);
		if (!group.exists()) {
			group.mkdir();
		}
		for (File newFile : files) {
			if (newFile != null) {
				if (!newFile.getParentFile().getAbsolutePath().equals(group.getAbsolutePath())) {
					String finalName = FileUtil.getNewFileName(group.getAbsolutePath(), newFile.getName());
					File finalFile = new File(group.getAbsolutePath() + File.separator + finalName);
					FileUtil.removeFile(newFile, finalFile, true);
					newFile = finalFile;
				}
				AttachFile aFile = new AttachFile();
				aFile.setId(StrUtil.getUUID());
				aFile.setGroupId(groupid);
				aFile.setName(FileUtil.getFileNameWithoutSuffix(newFile.getName()));
				aFile.setSuffix(FileUtil.getFileSuffix(newFile.getName()).toLowerCase());
				aFile.setPath(newFile.getAbsolutePath());
				aFile.setSize(new BigDecimal(newFile.length()));
				aFile.setCreateTime(DateUtil.getCurrentTime());
				// aFile.setUserid(userInfo == null ? "":userInfo.getUserId());
				// aFile.setUserName(userInfo == null ? "":userInfo.getUserName());
				if (insertFile(aFile) == null) {
					FileUtil.deleteFile(newFile);
				} else {
					list.add(aFile);
				}
			}
		}
		return list;
	}

	@Override
	public void downloadFile(HttpServletRequest request, HttpServletResponse response, String id, String pre)
			throws IOException {
		AttachFile rs = queryFile(id);
		File file = new File(rs.getPath());
		if (file.exists() && rs != null) {
			int length = Integer.MAX_VALUE;
			if (file.length() < length) {
				length = (int) file.length();
			}
			response.setContentLength(length);
			String fileName;
			if (null != pre && pre != "") {
				fileName = pre + "——" + rs.getName() + "." + rs.getSuffix();
			} else {
				fileName = file.getName();
			}
			FileUtil.downloadFilebBreakpoint(request, response, file, fileName);
		}
	}

	@Override
	public void previewFile(HttpServletRequest request, HttpServletResponse response, String id) throws IOException {
		previewFile(request, response, id, null, false);
	}

	@Override
	public void previewFile(HttpServletRequest request, HttpServletResponse response, String id, String size,
			boolean cut) throws IOException {
		AttachFile rs = queryFile(id);
		if (rs != null) {
			File file = new File(rs.getPath());
			if (size == null) {
				file = new File(rs.getPath());
			} else {
				file = FileUtil.resizeImage(new File(rs.getPath()),
						previewBasepath + id + "-" + size + "-" + cut + "." + rs.getSuffix(), size, cut);
			}
			if (file != null && file.exists()) {
				int length = Integer.MAX_VALUE;
				if (file.length() < length) {
					length = (int) file.length();
				}
				response.setContentLength(length);
				String fileName = file.getName();
				FileUtil.openFilebBreakpoint(request, response, file, fileName);
			}
		}
	}

	/**
	 * 添加一条新记录
	 * 
	 * @param elemType
	 *            添加的对象
	 * @return int 执行成功的数量
	 */
	@Override
	public AttachFile insertFile(AttachFile elemType) {
		return fileDao.save(elemType);
	}

	/**
	 * 根据主键删除一条新记录
	 * 
	 * @param id
	 *            Map<String,Object> 主键值
	 * @return int 执行成功的数量
	 */
	@Override
	public void deleteFile(String id) {
		try {
			AttachFile attenchFile = fileDao.findDataById(id);
			if (attenchFile == null)
				return;
			FileUtil.deleteFile(attenchFile.getPath());
			File path = new File(basepath + attenchFile.getGroupId());
			if (path.exists() && path.isDirectory()) {
				String[] fileList = new File(basepath + attenchFile.getGroupId()).list();
				if (fileList.length == 0) {
					FileUtil.deleteFile(basepath + attenchFile.getGroupId());
				}
			}
		} catch (DataAccessException e) {
			e.printStackTrace();
		}
		fileDao.deleteById(id);
	}

	/**
	 * 根据主键更新一条新记录
	 * @param groupId
	 * @param fileId
	 * @param file
	 * @return
	 */
	@Override
	public AttachFile updateFile(String groupId, String fileId, MultipartFile file) {
		// 获取当前登陆用户
		// UserInfo userInfo = URMSUtil.getCurrentUserInfo();

		AttachFile aFile = queryFile(fileId);
		if (aFile != null) {
			FileUtil.deleteFile(aFile.getPath());
			File group = new File(basepath + groupId);
			if (!group.exists()) {
				group.mkdir();
			}
			String filename = FileUtil.getNewFileName(basepath + groupId, file.getOriginalFilename());
			File newFile = null;
			try {
				newFile = FileUtil.getFile(file.getInputStream(), basepath + groupId + File.separator + filename);
				aFile.setSuffix(FileUtil.getFileSuffix(filename).toLowerCase());
				aFile.setPath(newFile.getAbsolutePath());
				aFile.setSize(new BigDecimal(newFile.length()));
				// aFile.setUserid(userInfo==null ?"":userInfo.getUserId());
				return fileDao.save(aFile);
			} catch (IOException e) {
				return null;
			}
		} else {
			return null;
		}
	}

	/**
	 * 根据主键查询一条附件表
	 * 
	 * @param key
	 *            主键
	 * @return 查询到的附件表对象
	 */
	@Override
	@Transactional(propagation = Propagation.NOT_SUPPORTED, readOnly = true)
	public AttachFile queryFile(String key) {
		return fileDao.findDataById(key);
	}

	/**
	 * 查询所有附件表记录
	 * 
	 * @return 附件表对象集
	 */
	@Override
	@Transactional(propagation = Propagation.NOT_SUPPORTED, readOnly = true)
	public List<AttachFile> queryFileList() {
		return fileDao.findAll();
	}

	/**
	 * 根据附件组编号删除多个附件表记录
	 * 
	 * @param groupId
	 *            Object
	 * @return int 执行成功的数量
	 */
	@Override
	public void deleteFileByGroupId(String groupId) {
		FileUtil.deleteFile(basepath + groupId);
		fileDao.deleteByGroupId(groupId);
	}

	/**
	 * 根据附件组编号删除多个附件表记录
	 * 
	 * @param groupId
	 *            Object
	 * @return int 执行成功的数量
	 */
	@Override
	public List<AttachFile> findByGroupId(String groupId) {
		return fileDao.findByGroupIdOrderByCreateTimeAsc(groupId);
	}

	/**
	 * 根据附件组进行下载组文件
	 */
	@Override
	public void downLoadByGroup(HttpServletRequest request, HttpServletResponse response, String groupId) {
		Date time = DateUtil.getCurrentTime();
		String groupFile = basepath + System.getProperty("file.separator") + "groupExport"+ DateUtil.dateToStringNormal2(time)
				+ System.getProperty("file.separator");
		
		List<AttachFile> list = findByGroupId(groupId);
		if (list != null && list.size() > 0) {
			// 进行文件复制
			for (AttachFile attachFile : list) {
				FileUtil.copyFile(attachFile.getPath(),
						groupFile + groupId + System.getProperty("file.separator") + attachFile.getName() + "."
								+ attachFile.getSuffix(),
						true);
			}
			//导出zip
			// 使用本工具类的readKeyValue方法
			ZipOutputStream out =  null;
			String filePath =  basepath + System.getProperty("file.separator")+"groupExport"+DateUtil.dateToStringNormal2(time);
			try {
				File file = new File(filePath+".zip");
				FileOutputStream fos1 = new FileOutputStream(file);
				FileUtil.toZip(new File(filePath), fos1,true);
				FileUtil.exportZip(response, file);
			} catch (IOException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (null != out) {
					try {
						out.flush();
						out.close();
					} catch (Exception e) {
					}
				}
			}
			//删除临时文件
			FileUtil.deleteFile(new File(filePath+".zip"));
			FileUtil.deleteFile(new File(groupFile));
		}
	}
}