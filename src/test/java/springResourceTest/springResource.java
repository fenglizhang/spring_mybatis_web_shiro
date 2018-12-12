package springResourceTest;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import junit.framework.Assert;

import org.junit.Test;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;

public class springResource {

	@Test
	public void testByteArrayResource() {
		Resource resource = new ByteArrayResource("Hello World!".getBytes());
		if (resource.exists()) {
			dumpStream(resource);
		}
	}

	private void dumpStream(Resource resource) {
		InputStream is = null;
		try {
			// 1.获取文件资源
			is = resource.getInputStream();
			// 2.读取资源
			byte[] descBytes = new byte[is.available()];
			is.read(descBytes);
			System.out.println(new String(descBytes));
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				// 3.关闭资源
				is.close();
			} catch (IOException e) {
			}
		}
	}
	
	
	
	
	
	
	
	
	@SuppressWarnings("deprecation")
	@Test  
	public void testInputStreamResource() {  
	   ByteArrayInputStream bis = new ByteArrayInputStream("Hello World!".getBytes());  
	   Resource resource = new InputStreamResource(bis);  
	    if(resource.exists()) {  
	       dumpStream(resource);  
	    }  
	    Assert.assertEquals(true, resource.isOpen());  
	}  
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
