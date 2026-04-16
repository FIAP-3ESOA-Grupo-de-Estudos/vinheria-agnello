<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
	<head>
		<meta charset="UTF-8">
		<title>Vinheria Agnelo</title>
		<link rel="stylesheet" href="assets/css/style.css" />
	</head>
	<body>
		<%@ include file="header.jsp" %>
		<main>
			<div class="container-wide">
				<%@ include file="hero.jsp" %>
				<%@ include file="weekly-highlights.jsp" %>
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
			<%@ include file="magazine.jsp" %>
			<%@ include file="subscription-plans.jsp" %>
			<%@ include file="footer.jsp" %>
		</main>    
	</body>
</html>