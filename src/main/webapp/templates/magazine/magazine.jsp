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
	<div class="magazine-highlights">
		<h1>Revista Agnello</h1>
		<h2 class="desktop-only">Cultura, conhecimento e paixão por vinhos</h2>
		<div class="mobile-only magazine-highlight-box">
			<span>✦ GIULIO ESCREVE</span>
			<h1>A Arte da Harmonização Perfeita</h1>
			<span>8 min de leitura</span>
		</div>
		<ul class="list-items desktop-only">
			<li>
				<img
					src="assets/images/article_650x350.png"
					alt="Magazine 1"
					class="highlight-image"
				/>
				<div class="item-details">
					<span class="category">✦ GIULIO ESCREVE</span>
					<h3>A Arte da Harmonização Perfeita</h3>
					<span class="reading-time">8 min de leitura</span>
				</div>
			</li>
			<li>
				<ul>
					<li>
						<img
							src="assets/images/article_2_140x140.png"
							alt="Magazine 1"
							class="highlight-image"
						/>
						<div class="item-details">
							<span class="category">✦ GIULIO ESCREVE</span>
							<h3>A Arte da Harmonização Perfeita</h3>
							<span class="reading-time">8 min de leitura</span>
						</div>
					</li>
					<li>
						<img
							src="assets/images/article_3_140x140.png"
							alt="Magazine 1"
							class="highlight-image"
						/>
						<div class="item-details">
							<span class="category">✦ GIULIO ESCREVE</span>
							<h3>A Arte da Harmonização Perfeita</h3>
							<span class="reading-time">8 min de leitura</span>
						</div>
					</li>
				</ul>
			</li>
		</ul>
	</div>
</section>