<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
		<%
			request.setAttribute("includedHeader", Boolean.TRUE);
		%>
		<%@ include file="templates/common/header.jsp" %>
		<main>
			<div class="container-wide">
				<%@ include file="templates/hero/hero.jsp" %>
				<%@ include file="templates/products/weekly-highlights.jsp" %>
				<section class="container">
					<div class="somelia-section">
						<img
							src="https://placehold.co/64x64"
							alt="SomelIA"
							class="newsletter-image"
						/>
						<div>
							<h1>Conheça a SomelIA</h1>
							<p>
								Sua sommelier virtual inteligente. Descubra vinhos perfeitos
								para<br />
								qualquer ocasião com recomendações personalizadas por IA.
							</p>
						</div>
						<button class="learn-more-btn">Experimentar Agora</button>
					</div>
				</section>
			</div>
			<%@ include file="templates/magazine/magazine.jsp" %>
			<%@ include file="templates/products/subscription-plans.jsp" %>
			<%@ include file="templates/common/footer.jsp" %>
		</main>    
	</body>
</html>