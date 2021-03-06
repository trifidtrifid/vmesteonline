package com.vmesteonline.be;

import java.io.IOException;
import java.nio.ByteBuffer;

import javax.jdo.PersistenceManager;

import com.vmesteonline.be.data.PMF;
import com.vmesteonline.be.utils.StorageHelper;

public class FileServiceImpl extends ServiceImpl implements FileService.Iface {

	@Override
	public String saveFileContent(ByteBuffer data, boolean isPublic) throws InvalidOperation {
		return copyFileContent( new String( data.array()), isPublic );
	}

	@Override
	public String copyFileContent(String sourceUrl, boolean isPublic) throws InvalidOperation {
		PersistenceManager pm = PMF.getPm();
		try {
			
			long uid = getCurrentUserId(pm);
			return StorageHelper.saveImage( sourceUrl, uid, isPublic, pm);
			
		} catch (IOException e) {
			e.printStackTrace();
			throw new InvalidOperation(VoError.IncorrectParametrs, "Failed to save image: "+e.getMessage());
		} finally {
			pm.close();
		}
	}

	@Override
	public String replaceFileFromURL(String oldUrl, String newSourceUrl) throws InvalidOperation {
		PersistenceManager pm = PMF.getPm();
		try {
			
			long uid = getCurrentUserId(pm);
			return StorageHelper.replaceImage(newSourceUrl, oldUrl, uid, null, pm);
			
		} catch (IOException e) {
			e.printStackTrace();
			throw new InvalidOperation(VoError.IncorrectParametrs, "Failed to replace image: "+e.getMessage());
		} finally {
			pm.close();
		}
	}

	@Override
	public String replaceFileContent(String oldUrl, ByteBuffer newData) throws InvalidOperation {
		return replaceFileFromURL( oldUrl, new String( newData.array()));
	}

	@Override
	public void deleteFile(String url) throws InvalidOperation {
		PersistenceManager pm = PMF.getPm();
		try {
			StorageHelper.deleteImage(url, pm);
		} catch (IOException e) {
			e.printStackTrace();
			throw new InvalidOperation(VoError.IncorrectParametrs, "Failed to delete image: "+e.getMessage());
		} finally {
			pm.close();
		}
	}
}
