package com.essence.business.xqh.web.waterandrain.controller;
import com.essence.business.xqh.api.systemMonitor.*;
import com.essence.business.xqh.common.returnFormat.SystemSecurityMessage;
import org.hyperic.sigar.*;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import java.net.InetAddress;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * @author NoBugNoCode
 * @date 2021/3/29 16:14
 */
@RestController
@RequestMapping(value = "/systemMonitor")
public class SystemMonitorController {

    /**
     * 获取服务器监控信息 （系统，磁盘，cpu,内存等）
     * @return
     */
    @RequestMapping(value = "/getSystemMonitorInfo", method = RequestMethod.GET)
    public SystemSecurityMessage getSystemMonitorInfo() {
        try {
            DecimalFormat df = new DecimalFormat("0.000");

            Sigar sigar = new Sigar();
            AllMnitorDto allMnitorDto = new AllMnitorDto();
            //获取系统信息
            SystemInfoDto systemInfoDto = new SystemInfoDto();
            allMnitorDto.setSystemInfo(systemInfoDto);
            //获取监控
            Runtime r = Runtime.getRuntime();
            Properties props = System.getProperties();
            InetAddress addr;
            addr = InetAddress.getLocalHost();
            String ip = addr.getHostAddress();
            Map<String, String> map = System.getenv();
            String userName = map.get("USERNAME");// 获取用户名
            String computerName = map.get("COMPUTERNAME");// 获取计算机名
            String userDomain = map.get("USERDOMAIN");// 获取计算机域名

            systemInfoDto.setComputerUser(userName);
            systemInfoDto.setComputerName(computerName);
            systemInfoDto.setComputerDomainName(userDomain);
            systemInfoDto.setIp(ip);
            systemInfoDto.setComputerLocalName(addr.getHostName());
            systemInfoDto.setJvmTotal(r.totalMemory()+"");
            systemInfoDto.setJvmFree(r.freeMemory()+"");
            systemInfoDto.setCpuCounts(r.availableProcessors()+"");
            systemInfoDto.setJvmEdition(props.getProperty("java.version"));
            systemInfoDto.setSystem(props.getProperty("os.name"));
            systemInfoDto.setSystemByte(props.getProperty("os.arch"));
            systemInfoDto.setSystemEdition(props.getProperty("os.version"));

            //获取磁盘信息
            List<DiskMnitorDto> diskList = new ArrayList<>();
            allMnitorDto.setDiskMnitor(diskList);
            FileSystem fslist[] = sigar.getFileSystemList();
            for (int i = 0; i < fslist.length; i++) {
                DiskMnitorDto diskMnitorDto = new DiskMnitorDto();
                diskList.add(diskMnitorDto);
                FileSystem fs = fslist[i];
                // 分区的盘符名称
                diskMnitorDto.setName(fs.getDevName());
                // 分区的盘符路径
                diskMnitorDto.setPath(fs.getDirName());
                // 文件系统类型，比如 FAT32、NTFS
                diskMnitorDto.setType(fs.getSysTypeName());
                // 文件系统类型名，比如本地硬盘、光驱、网络文件系统等
                diskMnitorDto.setLocal(fs.getTypeName());
                FileSystemUsage usage = null;
                try {
                    usage = sigar.getFileSystemUsage(fs.getDirName());
                }catch (SigarException e){
                    System.out.println("getFileSystemUsage..报错啦");
                    continue;
                }
                switch (fs.getType()) {
                    case 0: // TYPE_UNKNOWN ：未知
                        break;
                    case 1: // TYPE_NONE
                        break;
                    case 2: // TYPE_LOCAL_DISK : 本地硬盘
                        // 文件系统总大小
                        diskMnitorDto.setTotal(usage.getTotal()/1024L+"");
                        // 文件系统剩余大小
                        diskMnitorDto.setFree(fs.getDevName() + "剩余大小:    " + usage.getFree()/1024L + "MB");
                        // 文件系统可用大小
                        diskMnitorDto.setAvailable(fs.getDevName() + "可用大小:    " + usage.getAvail()/1024L + "MB");
                        // 文件系统已经使用量
                        double usePercent = usage.getUsePercent() * 100D;
                        // 文件系统资源的利用率
                        diskMnitorDto.setResourcesUsed(fs.getDevName() + "资源的利用率:    " + usePercent + "%");
                        break;
                    case 3:// TYPE_NETWORK ：网络
                        break;
                    case 4:// TYPE_RAM_DISK ：闪存
                        break;
                    case 5:// TYPE_CDROM ：光驱
                        break;
                    case 6:// TYPE_SWAP ：页面交换
                        break;
                }
                diskMnitorDto.setOutput(fs.getDevName() + "读出：    " + usage.getDiskReads());
                diskMnitorDto.setInput(fs.getDevName() + "写入：    " + usage.getDiskWrites());
            }

            //读取内存信息
            MemoryMnitorDto memoryMnitorDto = new MemoryMnitorDto();
            allMnitorDto.setMemoryMnitor(memoryMnitorDto);
            Mem mem = sigar.getMem();
            // 内存总量
            memoryMnitorDto.setTotal(mem.getTotal() / 1024L/1024L + "M av");
            // 当前内存使用量
            memoryMnitorDto.setUsed(mem.getUsed() / 1024L/1024L + "M used");
            // 当前内存剩余量
            memoryMnitorDto.setFree(mem.getFree() / 1024L/1024L + "M free");
            Swap swap = sigar.getSwap();
            // 交换区总量
            memoryMnitorDto.setExchangeTotal(swap.getTotal() / 1024L/1024L + "M av");
            // 当前交换区使用量
            memoryMnitorDto.setCurrentExchangeUsed(swap.getUsed() / 1024L/1024L + "M used");
            // 当前交换区剩余量
            memoryMnitorDto.setCurrentExchangeFree(swap.getFree() / 1024L/1024L + "M free");

            //读取cpu信息
            List<CpuMnitorDto> cpuList = new ArrayList<>();
            allMnitorDto.setCpuList(cpuList);
            CpuPerc perc = sigar.getCpuPerc();
            //获取当前总cpu的空闲率
            allMnitorDto.setCpuFree(df.format(perc.getIdle()*100));
            allMnitorDto.setCpuUsed(df.format(perc.getCombined()*100));

            CpuInfo infos[] = sigar.getCpuInfoList();
            CpuPerc cpuLists[] = null;
            cpuLists = sigar.getCpuPercList();
            for (int i = 0; i < infos.length; i++) {// 不管是单块CPU还是多CPU都适用
                CpuMnitorDto cpuMnitorDto = new CpuMnitorDto();
                cpuList.add(cpuMnitorDto);
                CpuInfo info = infos[i];
                cpuMnitorDto.setTotal(info.getMhz()+"");
                cpuMnitorDto.setCupMake(info.getVendor());
                cpuMnitorDto.setCpuVersion(info.getModel());
                CpuPerc cpu = cpuLists[i];
                cpuMnitorDto.setUserUsed("CPU用户使用率:    " + CpuPerc.format(cpu.getUser()));
                cpuMnitorDto.setSystemUsed("CPU系统使用率:    " + CpuPerc.format(cpu.getSys()));
                cpuMnitorDto.setWaitFor("CPU当前等待率:    " + CpuPerc.format(cpu.getWait()));
                cpuMnitorDto.setCpuFree("CPU当前空闲率:    " + CpuPerc.format(cpu.getIdle()));
                cpuMnitorDto.setTotalUsed("CPU总的使用率:    " + CpuPerc.format(cpu.getCombined()));
            }
            return SystemSecurityMessage.getSuccessMsg("获取服务器监控信息成功！", allMnitorDto);
        } catch (Exception e) {
            e.printStackTrace();
            return SystemSecurityMessage.getFailMsg("获取服务器监控信息失败！");

        }
    }
}


