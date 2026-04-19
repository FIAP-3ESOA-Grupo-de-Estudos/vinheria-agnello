<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%
  if (request.getAttribute("javax.servlet.include.request_uri") == null
      && !Boolean.TRUE.equals(request.getAttribute("includedHeader"))) {
    response.sendError(javax.servlet.http.HttpServletResponse.SC_FORBIDDEN);
    return;
  }
%>
<footer class="desktop-only">
	<div class="container">
	  <div class="footer-section">
	    <img src="assets/images/logo-white-no-bg.png" alt="Logo" class="logo" />
	    <ul>
	      <li><a href="index.jsp"><span class="mobile-only">🏠</span>Home</a></li>
            <li><a href="produto.jsp"><span class="mobile-only">🛒</span>Shop</a></li>
            <li><a href="catalogo.jsp"><span class="mobile-only">📋</span>Catálogo</a></li>
            <li><a href="sommelia.jsp"><span class="mobile-only">🤖</span>SomelIA</a></li>
            <!-- <li ><a href="index.jsp"><span class="mobile-only">🍷</span>Revista</a></li> -->
            <li><a href="perfil.jsp"><span class="mobile-only">👤</span>Perfil</a></li>
	    </ul>
	  </div>
	  <div class="copyright-section">
	    <p>© 2024 Vinheria Agnelo. Todos os direitos reservados.</p>
	  </div>
	</div>
</footer>