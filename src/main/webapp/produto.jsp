<%@ page language="java" contentType="text/html; charset=UTF-8"
pageEncoding="UTF-8" %>
    <% request.setAttribute("includedHeader", Boolean.TRUE); %> 
    <%@ include
    file="templates/common/header.jsp" %>
    <main style="background-color: #fff; box-shadow: 0 -4px 12px -8px rgba(0, 0, 0, 0.2);">
      <div class="container">
        <section class="product-section">
            <div class="main-product-wrapper">
                <img
                    src="assets/images/product-image.png"
                    alt="Wine Bottle"
                    class="product-image" />
                <div class="product-details">
                    <span class="recommended">★ GIULIO RECOMENDA</span>
                    <span class="wine-type">MALBEC · 2021</span>
                    <h1>Terrazas Reserva</h1>
                    <h2>Mendoza, Argentina</h2>
                    <span class="rating">★★★★★  4.8</span>
                    <span class="price desktop-only">R$ 189,00</span>
                    <div class="produt-btn-group mobile-only">
                        <span class="price">R$ 189,00</span>
                        <button class="buy-btn">Comprar Agora</button>
                    </div>
                    <button class="details-btn mobile-only">Adicionar ao clube</button> 
                    <div class="degustacao">
                        <h3>Notas de Degustação</h3>
                        <p>
                            Notas de ameixa madura, chocolate amargo e especiarias.
                            Taninos aveludados com final longo e elegante.
                        </p>
                    </div>
                    <div class="harmonizacao">
                        <h3>✦ HARMONIZAÇÃO</h3>
                        <p>
                            Perfeito com carnes vermelhas, queijos curados e pratos
                            condimentados. Ideal para churrascos e jantares especiais.
                        </p>
                    </div>
                    <div class="porque-escolhemos">
                        <h3>Por que escolhemos este vinho?</h3>
                        <p>
                            O Terrazas Reserva Malbec 2021 é uma expressão clássica
                            do terroir de Mendoza, oferecendo um equilíbrio perfeito
                            entre fruta, acidez e taninos. Sua complexidade e
                            elegância o tornam uma escolha excepcional para os amantes
                            de vinhos que buscam qualidade e personalidade.
                        </p>
                    </div>
                    <div class="produt-btn-group desktop-only">
                        <button class="buy-btn">Comprar Agora</button>
                        <button class="details-btn">Adicionar ao clube</button>
                    </div>
                    
                </div>
            </div>
            <div class="rating-section desktop-only">
                <h1>Avaliações dos clientes</h1>
                <ul>
                    <li>
                        <div>
                            <span>F</span>
                            <div>
                                <h1>Felipe</h1> 
                                <span class="ratings">
                                    ★★★★★ 5.0
                                </span>
                            </div>
                        </div>
                        <p class="comment">Gostei do produto.</p>
                    </li>
                    <li>
                        <div>
                            <span>A</span>
                            <div>
                                <h1>Ana</h1> 
                                <span class="ratings">
                                    ★★★★★ 5.0
                                </span>
                            </div>
                        </div>
                        <p class="comment">Amei!</p>
                    </li>
                    <li>
                        <div>
                            <span>F</span>
                            <div>
                                <h1>Bruno</h1> 
                                <span class="ratings">
                                    ★★★★★ 5.0
                                </span>
                            </div>
                        </div>
                        <p class="comment">Maravilhoso.</p>
                    </li>
                    
                </ul>
            </div>
            <div class="recomended-products-section desktop-only">
                <h1>Produtos Recomendados</h1>
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
      </div>
      <%@ include file="templates/common/footer.jsp" %>
    </main>
  </body>
</html>
