package spouts;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import backtype.storm.spout.SpoutOutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.IRichSpout;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Values;

public class WordReader implements IRichSpout {
	private static final long serialVersionUID = 1L;
	
    private final Logger logger = LoggerFactory.getLogger(getClass());

	private SpoutOutputCollector collector;
	private FileReader fileReader;
	private boolean completed = false;

	public boolean isDistributed() {
		return false;
	}

	/**
	 * 这是第一个方法，里面接收了三个参数，第一个是创建Topology时的配置，
	 * 第二个是所有的Topology数据，第三个是用来把Spout的数据发射给bolt
	 **/
	@Override
	public void open(Map conf, TopologyContext context, SpoutOutputCollector collector) {
		logger.info("*****************open()********************");
		
		try {
			// 获取创建Topology时指定的要读取的文件路径  
			this.fileReader = new FileReader(conf.get("wordsFile").toString());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			throw new RuntimeException("files error [" + conf.get("wordsFile") + "]");
//			throw new RuntimeException("Error reading file [" + conf.get("wordsFile") + "]");
		}
		// 初始化发射器
		this.collector = collector;

	}

	/**
	 * 这是Spout最主要的方法，在这里我们读取文本文件，并把它的每一行发射出去（给bolt）
	 * 这个方法会不断被调用，为了降低它对CPU的消耗，当任务完成时让它sleep一下
	 **/
	@Override
	public void nextTuple() {
		logger.info("*****************nextTuple()********************");

		if (completed) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// Do nothing
			}
			return;
		}
		String str;
		// Open the reader
		BufferedReader reader = new BufferedReader(fileReader);
		try {
			// Read all lines
			while ((str = reader.readLine()) != null) {
				/**
				 * 发射每一行，Values是一个ArrayList的实现
				 */
				this.collector.emit(new Values(str), str);
			}
		} catch (Exception e) {
			throw new RuntimeException("Error reading tuple", e);
		} finally {
			completed = true;
		}

	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		logger.info("*****************declareOutputFields()********************");

		declarer.declare(new Fields("line"));

	}

	@Override
	public void close() {
		logger.info("*****************close()********************");

		// TODO Auto-generated method stub
	}

	@Override
	public void activate() {
		logger.info("*****************activate()********************");

		// TODO Auto-generated method stub

	}

	@Override
	public void deactivate() {
		logger.info("*****************deactivate()********************");

		// TODO Auto-generated method stub

	}

	@Override
	public void ack(Object msgId) {
		logger.info("*****************ack()********************");

		System.out.println("OK:" + msgId);
	}

	@Override
	public void fail(Object msgId) {
		logger.info("*****************fail()********************");

		System.out.println("FAIL:" + msgId);

	}

	@Override
	public Map<String, Object> getComponentConfiguration() {
		logger.info("*****************getComponentConfiguration()********************");

		// TODO Auto-generated method stub
		return null;
	}
}