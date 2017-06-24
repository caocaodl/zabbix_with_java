package com.isoft.imon.topo.util;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.struts2.ServletActionContext;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import sun.security.jca.GetInstance;

import com.isoft.biz.dao.IDAO;
import com.isoft.biz.daoimpl.platform.topo.HostExpDAO;
import com.isoft.biz.daoimpl.platform.topo.LineDAO;
import com.isoft.biz.daoimpl.platform.topo.NodeDAO;
import com.isoft.biz.daoimpl.platform.topo.TPicDAO;
import com.isoft.biz.vo.platform.topo.LineVo;
import com.isoft.biz.vo.platform.topo.NodeVo;
import com.isoft.biz.vo.platform.topo.TCabNode;
import com.isoft.biz.vo.platform.topo.TPic;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.imon.topo.engine.discover.element.Host;
import com.isoft.iradar.Cphp;
import com.isoft.iradar.RadarContext;
import com.isoft.iradar.api.API;
import com.isoft.iradar.common.util.IMonConsts;
import com.isoft.iradar.core.utils.EasyServlet;
import com.isoft.iradar.core.utils.StringUtil;
import com.isoft.iradar.model.params.CHostGroupGet;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;

public class TopoUtil {

	public static final String LINE_STROKEWEIGHT_FINE = "1";
	public static final String LINE_STROKEWEIGHT_MEDIUM = "2";
	public static final String LINE_STROKEWEIGHT_COARSE = "3";

	public static final String DEFAULT_BACKGROUPPIC_ID = "-200";

	public static final String INIT_NODE_ID = "-1";
	public static final String INIT_LINE_ID = "-1";
	public static final String INIT_HOST_ID = "-1";

	public static final String INIT_NODE_THUMBNAIL_ID = "-100";

	public static final int cab_room_priority = 1;
	public static final int cab_cabinet_priority = 2;
	public static final int cab_server_priority = 3;

	public static final String TOPO_PIC_CABINET_CATEGORY = "cabinet";
	public static final String TOPO_PIC_ROOM_CATEGORY = "room";
	public static final String TOPO_PIC_BACKGROUP_CATEGORY = "backgroup";

	public static final String TOPO_TAGNAME_ROOM = "machineroom";
	public static final String TOPO_TAGNAME_CABINET = "cabinet";
	public static final String TOPO_TAGNAME_THUMBNAIL = "thumbnail";
	
	public static final int HOST_NORMAL_STATE = 0;
	public static final int HOST_WARN_STATE = 1;
	public static final int HOST_FAULT_STATE = 2;
	
	public static final int TOPO_NODE_WIDTH = 50;
	public static final int TOPO_NODE_HEIGHT = 30;
	
//	public static CArray<Long> groupsCA = CArray.array(IMonConsts.MON_SERVER_LINUX,IMonConsts.MON_SERVER_WINDOWS,IMonConsts.MON_STORAGE,IMonConsts.MON_NET_CISCO);
	public static CArray<Long> groupsCA = CArray.array(IMonConsts.MON_SERVER_LINUX,IMonConsts.MON_SERVER_WINDOWS,IMonConsts.MON_NET_CISCO);
	public static CArray<Long> tenantGroupsCA = CArray.array(IMonConsts.DISCOVERED_HOSTS,IMonConsts.MON_VM);
	public static int MON_SERVER_LINUX = Nest.as(IMonConsts.MON_SERVER_LINUX).asInteger();
	public static int MON_SERVER_WINDOWS = Nest.as(IMonConsts.MON_SERVER_WINDOWS).asInteger();
//	public static int MON_STORAGE = Nest.as(IMonConsts.MON_STORAGE).asInteger();
	public static int MON_NET = Nest.as(IMonConsts.MON_NET_CISCO).asInteger();
	
	public static final String webRootUrl = ServletActionContext
			.getServletContext().getRealPath("/");

	public static final String TOPO_PIC_URL = SysConfigHelper.CONTEXT_PATH
			+ "platform/iradar/";
	
	//TODO
	public static String getCloudHostImage(Object obj){
		return NodeVo.ICON_PATH+"virtmachine_linux_0.gif";
	}
	
	public static String getHostImage(Map param){
		String imageName = "";
		int category = Nest.value(param, "category").asInteger();
		if(MON_SERVER_LINUX == category)
			imageName = "server_linux_0.gif";
		else if(MON_SERVER_WINDOWS == category)
			imageName = "server_windows_1.gif";
//		else if(MON_STORAGE == category)
//			imageName = "storage_4.gif";
		else if(MON_NET == category)
			imageName = "switch_0.gif";
		else 
			imageName = "virtmachine_linux_0.gif";
		return NodeVo.ICON_PATH+imageName;
	}
	
	/**
	 * 对资产中心的设备进行拓扑Node的Json格式化
	 * 定制方法
	 * @param param
	 * @return
	 */
	public static String toJson(Map param){
		String hostId = Nest.value(param, "hostId").asString();
		String name   = Nest.value(param, "name").asString();
//	    String image  = TopoUtil.getCloudHostImage(new Object());
	    String image  = TopoUtil.getHostImage(param);
	    String category = Nest.value(param, "category").asString();
	    String type = Nest.value(param, "type").asString();
	    String ownerHost = Nest.value(param, "ownerHost").asString();
	    
	    return "{" +
	        "id : -1," +	
			"hostId:" + hostId + "," +
			"tbnailId:" + TopoUtil.INIT_NODE_THUMBNAIL_ID + "," +
			"tagName:'" + "_"+ hostId + "'," +
			"category:'" + category + "'," +
			"name:'" + name + "'," +
			"searchName:'" + name + "'," +
			"type:'" + type + "'," +
			"ownerHost:'" + ownerHost + "'," +
			"image:'"+ image + "'," +
			"width:"+TopoUtil.TOPO_NODE_WIDTH+"," +
			"height:"+TopoUtil.TOPO_NODE_HEIGHT+"," +
	    "}";
	    
	}
	
	public static String nodesToXml(List<NodeVo> nodes, IDAO dao) {
		StringBuffer s = new StringBuffer("");
		s.append("<process xmlns='http://jbpm.org/4.4/jpdl'>");
		HostExpDAO hostExpDao = new HostExpDAO(dao.getSqlExecutor());
		for (NodeVo node : nodes) {
			String image = "";
			if (isHostCategory(node.getCategory())) {
				Host host = hostExpDao.doHostExpLoadById(node.getHostId());
				image = node.getImage(host);
			} else if (node.getCategory().equals(TopoUtil.TOPO_TAGNAME_THUMBNAIL)) {
				image = NodeVo.ICON_PATH + "submap.png";
			}
			s.append("<" + node.getTagName() + " id='" + node.getNodeId()
					+ "' hostId='" + node.getHostId() + "' tbnailId='"
					+ node.getTbnailId() + "' g='" + node.getG()
					+ "' category='" + node.getCategory() + "' image='" + image
					+ "' name='" + node.getName() + "'>");
			List<LineVo> lines = node.getLines();
			for (LineVo line : lines) {
				s.append("<" + line.getTagName() + " to='" + line.getToNode()
						+ "' id='" + line.getLineId() + "' strokeweight='"
						+ line.getStrokeWeight() + "' tbnailId='"
						+ line.getTbnailId() + "' g='" + line.getG()
						+ "' name='' color=''>");
				s.append("</" + line.getTagName() + ">");
			}
			s.append("</" + node.getTagName() + ">");
		}
		s.append("</process>");
		return s.toString();
	}

	/**
	 * 根据hostId返回NodeVo
	 * 
	 * @param hostId
	 * @param nodes
	 * @return
	 */
	public static NodeVo getNodeVo(String hostId, List<NodeVo> nodes) {
		for (NodeVo node : nodes) {
			if (node.getHostId().equals(hostId)) {
				return node;
			}
		}
		return null;
	}

	/**
	 * 根据hostName返回LineVo
	 * 
	 * @param hostName
	 * @param lines
	 * @return
	 */
	public static LineVo getLineVo(String hostName, List<LineVo> lines) {
		for (LineVo line : lines) {
			if (hostName.equals(line.getToNode())) {
				return line;
			}
		}
		return null;
	}

	/**
	 * 
	 * @param nodeList
	 * @return
	 */
	public static List<NodeVo> nodeListToNodeVos(NodeList nodeList) {
		List<NodeVo> nodes = new ArrayList<NodeVo>();
		for (int i = 0, imax = nodeList.getLength(); i < imax; i++) {
			Node topoNode = nodeList.item(i);
			if (!topoNode.hasAttributes()) {
				continue;
			}
			NamedNodeMap attrs = topoNode.getAttributes();
			String nodeId = attrs.getNamedItem("id").getTextContent();
			String hostId = "";
			if (attrs.getNamedItem("hostId") != null) {
				hostId = attrs.getNamedItem("hostId").getTextContent();
			}
			NodeVo vo = new NodeVo();
			vo.setNodeId(nodeId);
			vo.setHostId(hostId);
			vo.setG(attrs.getNamedItem("g").getTextContent());
			vo.setTbnailId(attrs.getNamedItem("tbnailId").getTextContent());
			vo.setName(attrs.getNamedItem("name").getTextContent());
			vo.setTagName(topoNode.getNodeName());
			vo.setCategory(attrs.getNamedItem("category").getTextContent());
			if (topoNode.hasChildNodes()) {
				List<LineVo> lines = new ArrayList<LineVo>();
				NodeList lineList = topoNode.getChildNodes();
				for (int j = 0, jmax = lineList.getLength(); j < jmax; j++) {
					Node lineNode = lineList.item(j);
					if (!lineNode.hasAttributes()) {
						continue;
					}
					NamedNodeMap childAttrs = lineNode.getAttributes();
					LineVo lineVo = new LineVo();
					lineVo.setLineId(childAttrs.getNamedItem("id")
							.getTextContent());
					lineVo.setNodeId(nodeId);
					lineVo.setStrokeWeight(childAttrs.getNamedItem(
							"strokeweight").getTextContent());
					lineVo.setTbnailId(childAttrs.getNamedItem("tbnailId")
							.getTextContent());
					lineVo.setTagName(lineNode.getNodeName());
					lineVo.setToNode(childAttrs.getNamedItem("to")
							.getTextContent());
					if (childAttrs.getNamedItem("g") != null) {
						lineVo.setG(childAttrs.getNamedItem("g")
								.getTextContent());
					}
					lines.add(lineVo);
				}
				vo.setLines(lines);
			}
			nodes.add(vo);
		}
		return nodes;
	}

	public static List<TCabNode> nodeListToTCabNode(NodeList nodeList) {
		List<TCabNode> nodes = new ArrayList<TCabNode>();
		for (int i = 0, imax = nodeList.getLength(); i < imax; i++) {
			Node topoNode = nodeList.item(i);
			if (!topoNode.hasAttributes()) {
				continue;
			}

			NamedNodeMap attrs = topoNode.getAttributes();
			String picId = "";
			if (attrs.getNamedItem("picId") != null) {
				picId = attrs.getNamedItem("picId").getTextContent();
			}
			TCabNode tCabNode = new TCabNode();
			tCabNode.setNodeId(attrs.getNamedItem("id").getTextContent());
			tCabNode.setHostId(attrs.getNamedItem("hostId").getTextContent());
			tCabNode.setPicId(picId);
			tCabNode.setG(attrs.getNamedItem("g").getTextContent());
			tCabNode.setName(attrs.getNamedItem("name").getTextContent());
			tCabNode.setTagName(topoNode.getNodeName());
			tCabNode.setCategory(attrs.getNamedItem("category")
					.getTextContent());

			nodes.add(tCabNode);
		}
		return nodes;
	}

	public static String tCabNodesToXml(List<TCabNode> nodes, IDAO dao) {
		StringBuffer s = new StringBuffer("");
		s.append("<process xmlns='http://jbpm.org/4.4/jpdl'>");
		HostExpDAO hostExpDao = new HostExpDAO(dao.getSqlExecutor());
		for (TCabNode node : nodes) {
			String image = "";
			if (isHostCategory(node.getCategory())) {
				long group = Nest.as(node.getCategory()).asLong();
//				List<Map> list =hostExpDao.doHostgroup(node.getHostId());
//				String groupid=list.get(0).get("groupid").toString();
//				Long group=Long.parseLong(groupid);
				if(group==IMonConsts.MON_SERVER_LINUX
						||group==IMonConsts.MON_SERVER_WINDOWS
//						||group==IMonConsts.MON_STORAGE
						||group==IMonConsts.MON_NET_CISCO){
//					image=getHostImage(group);
					image = getHostImage(CArray.map("category",group));
					s.append("<" + node.getTagName() + " id='" + node.getNodeId()
							+ "' hostId='" + node.getHostId() + "' picId='"
							+ node.getPicId() + "' g='" + node.getG() + "' category='"
							+ node.getCategory() + "' image='" + image + "' name='"
							+ node.getName() + "'>");
					s.append("</" + node.getTagName() + ">");	
				}
			} else if (StringUtil.isNotEmpty(node.getPicId())) {
				TPicDAO picDao = new TPicDAO(dao.getSqlExecutor());
				Map<String, Object> tempMap = new HashMap<String, Object>();
				tempMap.put("id", node.getPicId());
				TPic pic = picDao.doTPicLoadByID(tempMap);
				image = TopoUtil.TOPO_PIC_URL + pic.getUrl();
				s.append("<" + node.getTagName() + " id='" + node.getNodeId()
						+ "' hostId='" + node.getHostId() + "' picId='"
						+ node.getPicId() + "' g='" + node.getG() + "' category='"
						+ node.getCategory() + "' image='" + image + "' name='"
						+ node.getName() + "'>");
				s.append("</" + node.getTagName() + ">");
			}
			
		}
		s.append("</process>");
		return s.toString();
	}

	/**
	 * 获取XML的节点列表
	 * 
	 * @param xml
	 * @return NodeList
	 */
	public static NodeList getXmlNodeList(String xml) {
		try {
			Document doc = DocumentBuilderFactory.newInstance()
					.newDocumentBuilder()
					.parse(new ByteArrayInputStream(xml.getBytes("UTF8")));
			return doc.getDocumentElement().getChildNodes();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 
	 * 通过对t_node的节点查询，返回相应的xml
	 * 
	 * @param param
	 * @param dao
	 * @return
	 * @throws ParseException
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static String doGetTopoXml(Map param, IDAO dao)
			throws ParseException {
		SQLExecutor executor = dao.getSqlExecutor();
		StringBuffer s = new StringBuffer("");
		s.append("<process xmlns='http://jbpm.org/4.4/jpdl'>");
		NodeDAO nodeDao = new NodeDAO(executor);
		HostExpDAO hostExpDao = new HostExpDAO(executor);
		List<NodeVo> nodes = nodeDao.doTNodeList(null, param);
		boolean isHostCategory = false;
		for (NodeVo node : nodes) {
			Host host = null;
			if (isHostCategory(node.getCategory())) {
				host = hostExpDao.doHostExpLoadById(node.getHostId());
				if(!Cphp.empty(host))
					isHostCategory = true;
				else
					continue;
			}
			s.append("<" + node.getTagName() + " id='" + node.getNodeId()
					+ "' hostId='" + node.getHostId() + "' tbnailId='"
					+ node.getTbnailId() + "' g='" + node.getG()
					+ "' searchName='" + node.getName() + "' name='"
					+ node.getName() + "'");
			if (isHostCategory) {
				param.put("hostId", node.getHostId());
				List<Map> hostInIradar = nodeDao.doNodeGetHostIdInIradar(param);
				String nameInIradar = "";
				if((!Cphp.empty(hostInIradar))&&Cphp.count(hostInIradar)>0){
					nameInIradar = Nest.as(Cphp.empty(hostInIradar.get(0).get("name"))?hostInIradar.get(0).get("host"):hostInIradar.get(0).get("name")).asString();
					s.append(" visibleName='" + nameInIradar + "'");
					s.append(" nameInIradar='" + nameInIradar + "'");
				}
				s.append(" image='" + node.getImage(host) + "'");
				s.append(" category='" + host.getCategory() + "'");
			} else {
				s.append(" image='" + NodeVo.ICON_PATH + "submap.png'");
				s.append(" category='thumbnail'");
			}
			s.append(">");
			// 开始添加节点下的连线信息
			param.put("nodeId", node.getNodeId());
			LineDAO lineDao = new LineDAO(executor);
			List<LineVo> lines = lineDao.doLineList(param);
			for (LineVo line : lines) {
				s.append("<" + line.getTagName() + " to='" + line.getToNode()
						+ "' id='" + line.getLineId() + "' strokeweight='"
						+ line.getStrokeWeight() + "' tbnailId='"
						+ line.getTbnailId() + "' g='" + line.getG()
						+ "' name='' color=''>");
				s.append("</" + line.getTagName() + ">");
			}
			s.append("</" + node.getTagName() + ">");
			isHostCategory = false;
		}
		s.append("</process>");
		return s.toString();
	}

	public static boolean containThumbnail(List<NodeVo> nodes) {
		for (NodeVo vo : nodes) {
			if (vo.getTagName().toLowerCase().equals("thumbnail")) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 通过线所属的nodeId、toNode、tbnailId来判断是否存在集合中
	 * 
	 * @param lines
	 * @param line
	 * @return
	 */
	public static boolean contains(List<LineVo> lines, LineVo line) {
		for (LineVo vo : lines) {
			if (vo.getNodeId().equals(line.getNodeId())
					&& vo.getToNode().equals(line.getToNode())
					&& vo.getTbnailId().equals(line.getTbnailId())) {
				return true;
			}
		}
		return false;
	}

	public static boolean isHostCategory(String category) {
		if (TopoUtil.TOPO_PIC_CABINET_CATEGORY.equals(category)
				|| TopoUtil.TOPO_PIC_ROOM_CATEGORY.equals(category)
				|| TopoUtil.TOPO_TAGNAME_THUMBNAIL.equals(category)) {
			return false;
		}
		return true;
	}

	/**
	 * 转化XML
	 * 
	 * @param xml
	 * @return
	 */
	public static String translateXml(String xml) {
		if (xml != null) {
			xml = xml.replaceAll("&lt;", "<").replaceAll("&gt;", ">")
					.replaceAll("&#039;", "'");
		}
		return xml;
	}

	private static final int BREVIARY_WIDTH = 360;
	private static final int BREVIARY_HEIGHT = 300;
	public static byte[] drawNetTopoBreviary(String topoId,
			SQLExecutor sqlExecutor) throws MalformedURLException, IOException {
		float maxWidth = 0.0F;
		float maxHeight = 0.0F;
		float minWidth = 10000.0F;
		float minHeight = 10000.0F;
		NodeDAO nodeDao = new NodeDAO(sqlExecutor);
		Map<String, Object> tempMap = new HashMap<String, Object>();
		tempMap.put("topoId", topoId);
		List<NodeVo> nodes = nodeDao.doTNodeList(null, tempMap);
		for (NodeVo node : nodes) {
			float x = Float.parseFloat(node.getX());
			float y = Float.parseFloat(node.getY());
			if (x > maxWidth)
				maxWidth = x;
			if (y > maxHeight)
				maxHeight = y;
			if (x < minWidth)
				minWidth = x;
			if (y < minHeight)
				minHeight = y;
		}
		minWidth -= 20.0F;
		minHeight -= 20.0F;
		maxWidth += 50.0F;
		maxHeight += 50.0F;
		float rate1 = 0.0F;
		float rate2 = 0.0F;
		if (BREVIARY_WIDTH > maxWidth - minWidth)
			rate1 = BREVIARY_WIDTH / (maxWidth - minWidth);
		else
			rate1 = (maxWidth - minWidth) / BREVIARY_WIDTH;
		if (BREVIARY_HEIGHT > maxHeight - minHeight)
			rate2 = BREVIARY_HEIGHT / (maxHeight - minHeight);
		else
			rate2 = (maxHeight - minHeight) / BREVIARY_HEIGHT;
		if (rate1 == 0.0F)
			rate1 = 1.0F;
		if (rate2 == 0.0F)
			rate2 = 1.0F;

		BufferedImage bi = new BufferedImage(BREVIARY_WIDTH, BREVIARY_HEIGHT, 1);
		Graphics2D g = bi.createGraphics();
		g.setBackground(Color.WHITE);
		g.clearRect(0, 0, BREVIARY_WIDTH, BREVIARY_HEIGHT);
		Map<String, Integer> xs = new HashMap<String, Integer>();
		Map<String, Integer> ys = new HashMap<String, Integer>();
		Map<String, String> names = new HashMap<String, String>();
		HostExpDAO hostExpDao = new HostExpDAO(sqlExecutor);
		for (NodeVo node : nodes) {
			try {
				Host host = null;
				String image = "";
				boolean isHostCategory = false;
				if (isHostCategory(node.getCategory())) {
					host = hostExpDao.doHostExpLoadById(node.getHostId());
					if(!Cphp.empty(host))
						isHostCategory = true;
					else
						continue;
				}
				if (isHostCategory)
					image = node.getImage(host);
				else
					image = NodeVo.ICON_PATH + "submap.png";
				String path = EasyServlet.getRealPath(RadarContext.getContext()
						.getRequest(), image
						.substring(SysConfigHelper.CONTEXT_PATH.length()));
				File _file = new File(path);
				Image src = ImageIO.read(_file);
				int x = getCoordinate(node.getX(), minWidth, rate1);
				int y = getCoordinate(node.getY(), minHeight, rate2);
				xs.put(node.getNodeId(), Integer.valueOf(x));
				ys.put(node.getNodeId(), Integer.valueOf(y));

				names.put(node.getName(), node.getNodeId());

				g.drawImage(src, x, y, src.getWidth(null) / 2,
						src.getHeight(null) / 2, null);
				x -= node.getName().length() / 2;
				y += src.getHeight(null);
				g.setColor(Color.BLACK);
				g.drawString(node.getName(), x, y);
			} catch (Exception localException) {
				localException.printStackTrace();
			}
		}
		List<LineVo> lines = new ArrayList<LineVo>();
		LineDAO lineDao = new LineDAO(sqlExecutor);
		tempMap.put("tbnailId", TopoUtil.INIT_NODE_THUMBNAIL_ID);
		lines = lineDao.doLineList(tempMap);
		for (LineVo line : lines) {
			try {
				String from = line.getNodeId();
				String to = names.get(line.getToNode());

				if (!xs.containsKey(from) || !ys.containsKey(from)
						|| !xs.containsKey(to) || !xs.containsKey(to)) {
					continue;
				}

				int startX = ((Integer) xs.get(from)).intValue() + 8;
				int startY = ((Integer) ys.get(from)).intValue() + 8;
				int endX = ((Integer) xs.get(to)).intValue() + 8;
				int endY = ((Integer) ys.get(to)).intValue() + 8;
				g.setColor(Color.BLUE);
				g.drawLine(startX, startY, endX, endY);
			} catch (Exception localException1) {
				localException1.printStackTrace();
			}
		}
		g.dispose();
		bi.flush();
		return convertBytes(bi);
	}

	/**
	 * 获取坐标
	 * 
	 * @param xy
	 * @param minValue
	 * @param rate
	 * @return
	 */
	private static int getCoordinate(String xy, float minValue, float rate) {
		return (int) ((Float.parseFloat(xy) - minValue) / rate);
	}

	/**
	 * 生成视图图片
	 * 
	 * @param bi
	 * @param jgpName
	 * @return
	 * @throws IOException
	 */
	public static byte[] convertBytes(BufferedImage bi) throws IOException {
		ByteArrayOutputStream outFile = null;
		try {
			outFile = new ByteArrayOutputStream();
			ImageIO.write(bi, "jpg", outFile);
			return outFile.toByteArray();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (outFile != null) {
					outFile.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	public static String ICON_PATH = SysConfigHelper.CONTEXT_PATH + "assets/topo/images/ims/icon/";

	public static String getHostImage(Long categoryName) {
		String icon = "hint_bell2.png";
		if (IMonConsts.MON_SERVER_WINDOWS==categoryName) {
			icon = "server_windows_0.gif";
		} else if (IMonConsts.MON_SERVER_LINUX==categoryName) {
			icon = "server_linux_0.gif";
		} 
//		else if (IMonConsts.MON_STORAGE==categoryName) {
//			icon = "storage_0.gif";
//		}
		else if (IMonConsts.MON_NET_CISCO==categoryName) {

			icon = "router0.gif";
		}
		return ICON_PATH + icon;
	}
	
	/**
	 * 特殊字符处理
	 */
	public static CArray<String> specialCharacters = CArray.array("_","%",".","-","*","^","$","[","]","{","}","'");
	public static String doSpecialCharacters(String special){
		for(String s:specialCharacters){
			special = special.replace(s, "\\"+s);
		}
		return special;
	}
}
