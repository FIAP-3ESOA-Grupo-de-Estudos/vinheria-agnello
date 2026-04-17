<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%
  if (request.getAttribute("javax.servlet.include.request_uri") == null
      && !Boolean.TRUE.equals(request.getAttribute("includedHeader"))) {
    response.sendError(javax.servlet.http.HttpServletResponse.SC_FORBIDDEN);
    return;
  }
%>
<section class="container">
	<div class="subscription-plans">
		<h1>Planos de Assinatura</h1>
		<h2>Escolha o plano perfeito para receber vinhos selecionados</h2>
		<ul class="plans-list">
			<li class="plan-item">
				<h3>Iniciante</h3>
				<span class="price">R$ 89 <span>/mês</span></span>
				<span class="description">Para quem está descobrindo</span>
				<ul>
					<li>2 garrafas</li>
					<li>Revista digital</li>
					<li>Guia de harmonização</li>
				</ul>
				<button class="subscribe-btn">Assinar Agora</button>
			</li>
			<li class="plan-item highlight">
				<h3>Explorador</h3>
				<span class="price">R$ 179 <span>/mês</span></span>
				<span class="description">Para quem está descobrindo</span>
				<ul>
					<li>4 garrafas + 1 surpresa</li>
					<li>SomelIA personalizada</li>
					<li>Comunidade + 10% off</li>
				</ul>
				<button class="subscribe-btn">Assinar Agora</button>
			</li>
			<li class="plan-item">
				<h3>Sommelier</h3>
				<span class="price">R$ 299 <span>/mês</span></span>
				<span class="description">Para quem está descobrindo</span>
				<ul>
					<li>6 garrafas premium</li>
					<li>Degustações com Giulio</li>
					<li>Frete grátis + benefícios</li>
				</ul>
				<button class="subscribe-btn">Assinar Agora</button>
			</li>
		</ul>
	</div>
</section>
