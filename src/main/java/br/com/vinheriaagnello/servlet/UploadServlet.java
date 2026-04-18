package br.com.vinheriaagnello.servlet;

import br.com.vinheriaagnello.service.StorageService;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

/**
 * Servlet implementation class UploadServlet
 */
@WebServlet("/upload")
public class UploadServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	private final StorageService storageService = new StorageService();
	
	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            Part filePart = request.getPart("file");
            String fileName = filePart.getSubmittedFileName();
            String url = storageService.uploadFile(filePart.getInputStream(), fileName);
            request.getSession().setAttribute("lastUpload", url);
            response.sendRedirect("home.jsp?uploaded=" + fileName);
        } catch (Exception e) {
        	response.sendRedirect("home.jsp?error=upload_failed");
        }
	}

}
