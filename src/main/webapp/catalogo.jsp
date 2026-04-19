<%@ page language="java" contentType="text/html; charset=UTF-8"
pageEncoding="UTF-8" %>
    <% request.setAttribute("includedHeader", Boolean.TRUE); %> 
    <%@ include
    file="templates/common/header.jsp" %>
    <main>
      <div class="container">
        <section class="catalog-section">
          <h1>Catálogo de Vinhos</h1>
          <h2>Seleção curatorizada por Giulio · 156 rótulos</h2>
          <section class="search">
            <input
              type="text"
              placeholder="Buscar vinhos, uvas, regiões, safras..."
              class="search-input"
            />
            <ul>
              <li class="active">tag 1</li>
              <li>tag 2</li>
              <li>tag 3</li>
              <li>tag 4</li>
              <li>tag 5</li>
            </ul>
          </section>

          <section class="highlights">
            <img src="https://placehold.co/100x130" alt="Destaque do Mês" class="highlight-image" />
            <div class="description">
                <span class="top-label">✦ Descoberta do mês</span>
                <h2>Casa Valduga Gran Leopoldina</h2>
                <p class="desktop-only">
                Cabernet Sauvignon · Vale dos Vinhedos, Brasil · 2020<br/>
                Encorpado e complexo, com aromas de frutas negras, baunilha e tabaco.
                </p>
            </div>
            <div class="details">
                <span class="price">R$ 189,00</span>
                <span class="rating">★★★★★ 4.9</span>
                <button class="buy-btn">Comprar Agora</button>
            </div>
          </section>
          <ul class="list-items">
            <li>
              <img
                src="https://placehold.co/225x211"
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
                src="https://placehold.co/225x211"
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
                src="https://placehold.co/225x211"
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
                src="https://placehold.co/225x211"
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
                src="https://placehold.co/225x211"
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
                src="https://placehold.co/225x211"
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
                src="https://placehold.co/225x211"
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
                src="https://placehold.co/225x211"
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
        </section>
      </div>
      <%@ include file="templates/common/footer.jsp" %>
    </main>
  </body>
</html>
