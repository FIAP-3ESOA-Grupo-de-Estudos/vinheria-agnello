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
					<div class="somelia-section" onclick="window.location.href='sommelia.jsp'">
						<img
							src="assets/images/robot.png"
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
						<button class="learn-more-btn desktop-only" onclick="window.location.href='sommelia.jsp'">Experimentar Agora</button>
						<button class="learn-more-btn mobile-only" onclick="window.location.href='sommelia.jsp'">></button>
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