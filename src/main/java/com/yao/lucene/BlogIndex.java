package com.yao.lucene;

import java.io.StringReader;
import java.nio.file.Paths;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.cn.smart.SmartChineseAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.highlight.Fragmenter;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.highlight.SimpleHTMLFormatter;
import org.apache.lucene.search.highlight.SimpleSpanFragmenter;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import com.yao.entity.Blog;
import com.yao.util.DateUtil;
import com.yao.util.StringUtil;

/**
 * ����������
 * @author Administrator
 *
 */
public class BlogIndex {

	private Directory dir;
	
	private static final String p = "C:\\lucene" ;

	public static String getP() {
		return p;
	}

	private IndexWriter getWriter()throws Exception{
		dir=FSDirectory.open(Paths.get(p));
		SmartChineseAnalyzer analyzer=new SmartChineseAnalyzer();
		IndexWriterConfig iwc=new IndexWriterConfig(analyzer);
		IndexWriter writer=new IndexWriter(dir, iwc);
		return writer;
	}
	
	/**
	 * ��Ӳ�������
	 * @param blog
	 * @throws Exception
	 */
	public void addIndex(Blog blog)throws Exception{
		IndexWriter writer=getWriter();
		Document doc=new Document();
		doc.add(new StringField("id",String.valueOf(blog.getId()),Field.Store.YES));
		doc.add(new TextField("title",blog.getTitle(),Field.Store.YES));
		if(null == blog.getReleaseDate()){
			doc.add(new StringField("releaseDate",DateUtil.formatDate(new Date(), "yyyy-MM-dd"),Field.Store.YES));
		}else{
			doc.add(new StringField("releaseDate",DateUtil.formatDate(blog.getReleaseDate(), "yyyy-MM-dd"),Field.Store.YES));
		}

		doc.add(new TextField("content",blog.getContentNoTag(),Field.Store.YES));
		writer.addDocument(doc);
		writer.close();
	}
	
	/**
	 * ɾ��ָ�����͵�����
	 * @param blogId
	 * @throws Exception
	 */
	public void deleteIndex(String blogId)throws Exception{
		IndexWriter writer=getWriter();
		writer.deleteDocuments(new Term("id",blogId));
		writer.forceMergeDeletes(); // ǿ��ɾ��
		writer.commit();
		writer.close();
	}
	
	/**
	 * ���²�������
	 * @param blog
	 * @throws Exception
	 */
	public void updateIndex(Blog blog)throws Exception{
		IndexWriter writer=getWriter();
		Document doc=new Document();
		doc.add(new StringField("id",String.valueOf(blog.getId()),Field.Store.YES));
		doc.add(new TextField("title",blog.getTitle(),Field.Store.YES));
		doc.add(new StringField("releaseDate",DateUtil.formatDate(new Date(), "yyyy-MM-dd"),Field.Store.YES));
		doc.add(new TextField("content",blog.getContentNoTag(),Field.Store.YES));
		writer.updateDocument(new Term("id",String.valueOf(blog.getId())), doc);
		writer.close();
	}
	
	/**
	 * ��ѯ������Ϣ
	 * @param q
	 * @return
	 * @throws Exception
	 */
	public List<Blog> searchBlog(String q)throws Exception{
		dir=FSDirectory.open(Paths.get(p));
		IndexReader reader=DirectoryReader.open(dir);
		IndexSearcher is=new IndexSearcher(reader);
		BooleanQuery.Builder booleanQuery=new BooleanQuery.Builder();
		SmartChineseAnalyzer analyzer=new SmartChineseAnalyzer();
		
		QueryParser parser=new QueryParser("title", analyzer);
		Query query=parser.parse(q);
		
		QueryParser parser2=new QueryParser("content", analyzer);
		Query query2=parser2.parse(q);
		
		booleanQuery.add(query, BooleanClause.Occur.SHOULD);
		booleanQuery.add(query2, BooleanClause.Occur.SHOULD);
		
		TopDocs hits=is.search(booleanQuery.build(), 100);
		QueryScorer scorer=new QueryScorer(query);
		Fragmenter fragmenter=new SimpleSpanFragmenter(scorer);
		SimpleHTMLFormatter simpleHTMLFormatter=new SimpleHTMLFormatter("<b><font color='red'>", "</font></b>");
		Highlighter highlighter=new Highlighter(simpleHTMLFormatter, scorer);
		highlighter.setTextFragmenter(fragmenter);
		
		
		
		List<Blog> blogList=new LinkedList<Blog>();
		for(ScoreDoc scoreDoc:hits.scoreDocs){
			Document doc=is.doc(scoreDoc.doc);
			Blog blog=new Blog();
			blog.setId(Integer.parseInt(doc.get("id")));
			blog.setReleaseDateStr(doc.get("releaseDate"));
			String title=doc.get("title");
			
//			String content= doc.get("content");
			String content=StringEscapeUtils.escapeHtml(doc.get("content"));
			System.out.println("title:---"+title);
			System.out.println("content"+content);
			//���ϻ�ȡ�������е�����;
			
			//����title���зִʺ�Ĺؼ���,�ҳ����Ƭ��
			if(title!=null){
				TokenStream tokenStream=analyzer.tokenStream("title", new StringReader(title));
				String hTitle=highlighter.getBestFragment(tokenStream, title);
				if(StringUtil.isEmpty(hTitle)){
					blog.setTitle(title);
				}else{
					blog.setTitle(hTitle);
				}
			}
			
			//���� content ���зִʺ�Ĺؼ���,�ҳ����Ƭ��
			if(content!=null){
				TokenStream tokenStream=analyzer.tokenStream("content", new StringReader(content));
				String hContent=highlighter.getBestFragment(tokenStream, content);
				if(StringUtil.isEmpty(hContent)){
					if(content.length()<=200){
						blog.setContent(content);						
					}else{
						blog.setContent(content.substring(0, 200));	
					}
				}else{
					blog.setContent(hContent);
				}
			}
			blogList.add(blog);
		}
		return blogList;
	}
	
}
