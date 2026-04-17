<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%
  if (request.getAttribute("javax.servlet.include.request_uri") == null
      && !Boolean.TRUE.equals(request.getAttribute("includedHeader"))) {
    response.sendError(javax.servlet.http.HttpServletResponse.SC_FORBIDDEN);
    return;
  }
%>
<footer>
	<div class="container">
	  <div class="footer-section">
	    <img src="assets/images/logo-white-no-bg.png" alt="Logo" class="logo" />
	    <ul>
	      <li><a href="/shop.html">Shop</a></li>
	      <li><a href="/catalogo">Catálogo</a></li>
	      <li><a href="/somel.html">SomelIA</a></li>
	      <li><a href="/magazine.html">Revista</a></li>
	      <li><a href="/community.html">Comunidade</a></li>
	    </ul>
	  </div>
	  <div class="copyright-section">
	    <p>© 2024 Vinheria Agnelo. Todos os direitos reservados.</p>
	  </div>
	</div>
</footer>