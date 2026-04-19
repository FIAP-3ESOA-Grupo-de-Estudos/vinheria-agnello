<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%
  if (request.getAttribute("javax.servlet.include.request_uri") == null
      && !Boolean.TRUE.equals(request.getAttribute("includedHeader"))) {
    response.sendError(javax.servlet.http.HttpServletResponse.SC_FORBIDDEN);
    return;
  }
%>
<section class="hero">
	<img
		src="assets/images/hero-image.png"
		alt="Hero Image"
		class="hero-image"
	/>
	<div class="hero-content">
		<span class="label">★ RECOMENDAÇÃO DO GIULIO ESTA SEMANA</span>
		<h1>Terrazas Reserva Malbec 2021</h1>
		<h2>Mendoza, Argentina</h2>
		<p class="desktop-only">
			Notas de ameixa madura, chocolate amargo e especiarias. Taninos
			aveludados com final longo e elegante.
		</p>
		<span class="price">R$ 189,00</span>
		<div class="buttons">
			<button class="buy-btn"  onclick="window.location.href='carrinho.jsp'">Comprar Agora</button>
			<button class="details-btn desktop-only"  onclick="window.location.href='produto.jsp'">Ver Detalhes</button>
		</div>
	</div>
</section>
