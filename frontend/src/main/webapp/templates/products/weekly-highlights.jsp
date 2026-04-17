<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%
  if (request.getAttribute("javax.servlet.include.request_uri") == null
      && !Boolean.TRUE.equals(request.getAttribute("includedHeader"))) {
    response.sendError(javax.servlet.http.HttpServletResponse.SC_FORBIDDEN);
    return;
  }
%>
<section class="weekly-highlights">
	<div class="container">
		<h1>Destaque da Semana</h1>
		<h2>Seleção curatorizada por Giulio</h2>
		<ul class="list-items">
			<li>
				<img
					src="https://placehold.co/225x225"
					alt="Wine 1"
					class="highlight-image"
				/>
				<div class="item-details">
					<h3>Château Margaux 2015</h3>
					<p>Bordeaux, França</p>
					<span class="rating">★ 4.8</span>
					<span class="price">R$ 189,00</span>
				</div>
			</li>
			<li>
				<img
					src="https://placehold.co/225x225"
					alt="Wine 1"
					class="highlight-image"
				/>
				<div class="item-details">
					<h3>Château Margaux 2015</h3>
					<p>Bordeaux, França</p>
					<span class="rating">★ 4.8</span>
					<span class="price">R$ 189,00</span>
				</div>
			</li>
			<li>
				<img
					src="https://placehold.co/225x225"
					alt="Wine 1"
					class="highlight-image"
				/>
				<div class="item-details">
					<h3>Château Margaux 2015</h3>
					<p>Bordeaux, França</p>
					<span class="rating">★ 4.8</span>
					<span class="price">R$ 189,00</span>
				</div>
			</li>
			<li>
				<img
					src="https://placehold.co/225x225"
					alt="Wine 1"
					class="highlight-image"
				/>
				<div class="item-details">
					<h3>Château Margaux 2015</h3>
					<p>Bordeaux, França</p>
					<span class="rating">★ 4.8</span>
					<span class="price">R$ 189,00</span>
				</div>
			</li>
		</ul>
	</div>
</section>