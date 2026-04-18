<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
		<%
			request.setAttribute("includedHeader", Boolean.TRUE);
		%>
		<%@ include file="templates/common/header.jsp" %>
		<main>
			<div class="container-wide">
				<div class="welcome-home mobile-only">
					        <a href="index.jsp"><img src="assets/images/logo.png" alt="Logo" class="logo" /></a> 

				</div>
				<%@ include file="templates/hero/hero.jsp" %>
				<%@ include file="templates/products/weekly-highlights.jsp" %>
				<section class="order-container container">
					<div class="somelia-section">
						<img
							src="https://placehold.co/64x64"
							alt="SomelIA"
							class="newsletter-image"
						/>
						<div>
							<h1>Conheça a SomelIA</h1>
							<p class="desktop-only">
								Sua sommelier virtual inteligente. Descubra vinhos perfeitos
								para<br />
								qualquer ocasião com recomendações personalizadas por IA.
							</p>
							<p class="mobile-only">Descubra o vinho perfeito para hoje</p>
						</div>
						<button class="learn-more-btn desktop-only">Experimentar Agora</button>
						<button class="learn-more-btn mobile-only">></button>
					</div>
					
				</section>
				
			</div>
			<%@ include file="templates/magazine/magazine.jsp" %>
			<%@ include file="templates/products/subscription-plans.jsp" %>
			<div class="community-section-home mobile-only">
						<h1>Comunidade</h1>
						<h2>O que os membros estão bebendo</h2>
						<ul>
							<li>"Catena Zapata Chardonnay — Perfeito para o calor! 🌞" - <span>Marcos R.</span></li>
							<li>Lidio Carraro Dádivas — Harmonizei com churrasco gaúcho!" - <span>Ana L.</span></li>

						</ul>
					</div>
			<%@ include file="templates/common/footer.jsp" %>
		</main>    
	</body>
</html>