<%--
 * index.jsp
 *
 * Copyright (C) 2017 Universidad de Sevilla
 * 
 * The use of this project is hereby constrained to the conditions of the 
 * TDG Licence, a copy of which you may download from 
 * http://www.tdg-seville.info/License.html
 --%>

<%@page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>

<%@taglib prefix="jstl"	uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib prefix="security" uri="http://www.springframework.org/security/tags"%>
<%@taglib prefix="display" uri="http://displaytag.sf.net"%>

 <jstl:choose>
 <jstl:when test="${pageContext.response.locale.language=='en'}">
				<b> TERMS AND CONDITIONS </b>
 
<p> This document sets out the terms on which Acme Newspaper uses and protects the information that is provided by its users when using your website. This company is committed to the security of its users' data. When we ask you to fill in the fields of personal information with which you can be identified, we do so by ensuring that it will only be used in accordance with the terms of this document. However, these Terms of Use may change over time or be updated for what we recommend and we strongly encourage you to continually review this page to ensure that you agree to such changes. <P>
<i> <b> Information that is collected </b> </i>

<p> Our website may collect personal information such as: Name, contact information such as your email address and demographic information. Also when necessary, specific information may be required to process an order or make a delivery or billing. </p>
<i> <b> Use of the information collected </b> </i>

<p> Our website uses the information in order to provide the best possible service, particularly to maintain a register of users, orders if applicable, and improve our products and services. It is possible that periodic emails will be sent through our site with special offers, new products and other advertising information that we consider relevant to you or that may benefit you, these emails will be sent to the address you provide and may be canceled. anytime.

Acme Newspaper is highly committed to fulfilling the commitment to keep your information secure. We use the most advanced systems and update them constantly to ensure that there is no unauthorized access. </p>
<i> <b> Cookies </b> </i>

<p> A cookie refers to a file that is sent for the purpose of requesting permission to be stored on your computer, when accepting said file is created and the cookie then serves to have information regarding web traffic, and also facilitates future visits to a recurring website. Another function that cookies have is that with them the web can recognize you individually and therefore provide you with the best personalized service on your website.

Our website uses cookies to identify the pages that are visited and their frequency. This information is used only for statistical analysis and then the information is permanently deleted. You can delete cookies at any time from your computer. However, cookies help to provide a better service to websites, you are not given access to information from your computer or from you, unless you want it and provide it directly. You can accept or deny the use of cookies, however most browsers automatically accept cookies because it serves to have a better web service. You can also change the configuration of your computer to decline cookies. If they decline, you may not be able to use some of our services. </p>

<i> <b> Links to Third Parties </b> </i>

<p> This website may contain links to other sites that may be of interest to you. Once you click on these links and leave our page, we no longer have control over the site to which you are redirected and therefore we are not responsible for the terms or privacy or the protection of your data in those other third party sites. These sites are subject to their own privacy policies, so it is recommended that you check them to confirm that you agree with them. </p>

<i> <b> Inappropiate content</b> </i>

<p> The admin may delete content that contains taboo words or can be considered offensive.</p>

<i> <b> Control of your personal information </b> </i>

<p>You can edit the information provided to the system at any time by clicking on the personal edit button on your profile and deleting your account using the delete account button. 
At any time you can restrict the collection or use of personal information that is provided to our website. Each time you are asked to fill in a form, such as the user registration form, you can check or uncheck the option to receive information by email. In case you have marked the option to receive our newsletter or advertising you can cancel it at any time.

This company will not sell, assign or distribute personal information that is collected without your consent, unless required by a judge with a court order. </p>

<p> Acme Newspaper reserves the right to change the terms and conditions at any time. </p>

 </jstl:when>
 <jstl:otherwise>

                <b>TERMINOS Y CONDICIONES</b>
 
<p>El presente documento establece los términos en que Acme Newspaper usa y protege la información que es proporcionada por sus usuarios al momento de utilizar su sitio web. Esta compañía está comprometida con la seguridad de los datos de sus usuarios. Cuando le pedimos llenar los campos de información personal con la cual usted pueda ser identificado, lo hacemos asegurando que sólo se empleará de acuerdo con los términos de este documento. Sin embargo estos Terminos de uso pueden cambiar con el tiempo o ser actualizados por lo que le recomendamos y enfatizamos revisar continuamente esta página para asegurarse que está de acuerdo con dichos cambios.<p>

<i><b>Información que es recogida</b></i>

<p>Nuestro sitio web podrá recoger información personal por ejemplo: Nombre,  información de contacto como  su dirección de correo electrónica e información demográfica. Así mismo cuando sea necesario podrá ser requerida información específica para procesar algún pedido o realizar una entrega o facturación.</p>

<i><b>Uso de la información recogida</b></i>

<p>Nuestro sitio web emplea la información con el fin de proporcionar el mejor servicio posible, particularmente para mantener un registro de usuarios, de pedidos en caso que aplique, y mejorar nuestros productos y servicios.  Es posible que sean enviados correos electrónicos periódicamente a través de nuestro sitio con ofertas especiales, nuevos productos y otra información publicitaria que consideremos relevante para usted o que pueda brindarle algún beneficio, estos correos electrónicos serán enviados a la dirección que usted proporcione y podrán ser cancelados en cualquier momento.

Acme Newspaper está altamente comprometido para cumplir con el compromiso de mantener su información segura. Usamos los sistemas más avanzados y los actualizamos constantemente para asegurarnos que no exista ningún acceso no autorizado.</p>

<i><b>Cookies</b></i>

<p>Una cookie se refiere a un fichero que es enviado con la finalidad de solicitar permiso para almacenarse en su ordenador, al aceptar dicho fichero se crea y la cookie sirve entonces para tener información respecto al tráfico web, y también facilita las futuras visitas a una web recurrente. Otra función que tienen las cookies es que con ellas las web pueden reconocerte individualmente y por tanto brindarte el mejor servicio personalizado de su web.

Nuestro sitio web emplea las cookies para poder identificar las páginas que son visitadas y su frecuencia. Esta información es empleada únicamente para análisis estadístico y después la información se elimina de forma permanente. Usted puede eliminar las cookies en cualquier momento desde su ordenador. Sin embargo las cookies ayudan a proporcionar un mejor servicio de los sitios web, estás no dan acceso a información de su ordenador ni de usted, a menos de que usted así lo quiera y la proporcione directamente. Usted puede aceptar o negar el uso de cookies, sin embargo la mayoría de navegadores aceptan cookies automáticamente pues sirve para tener un mejor servicio web. También usted puede cambiar la configuración de su ordenador para declinar las cookies. Si se declinan es posible que no pueda utilizar algunos de nuestros servicios.</p>

<i><b>Enlaces a Terceros</b></i>

<p>Este sitio web pudiera contener en laces a otros sitios que pudieran ser de su interés. Una vez que usted de clic en estos enlaces y abandone nuestra página, ya no tenemos control sobre al sitio al que es redirigido y por lo tanto no somos responsables de los términos o privacidad ni de la protección de sus datos en esos otros sitios terceros. Dichos sitios están sujetos a sus propias políticas de privacidad por lo cual es recomendable que los consulte para confirmar que usted está de acuerdo con estas.</p>

<i> <b>Contenido inapropiado</b> </i>

<p> El administrador podrá borrar contenido que contenga palabras tabú o que pueda considerarse ofensivo.</p>

<i><b>Control de su información personal</b></i>

<p>Puede editar la información proporcionada al sistema en cualquier momento mediante el botón de editar personales que se encuentra en su perfil así como borrar su cuenta mediante el boton de borrar cuenta.
En cualquier momento usted puede restringir la recopilación o el uso de la información personal que es proporcionada a nuestro sitio web.  Cada vez que se le solicite rellenar un formulario, como el de alta de usuario, puede marcar o desmarcar la opción de recibir información por correo electrónico.  En caso de que haya marcado la opción de recibir nuestro boletín o publicidad usted puede cancelarla en cualquier momento.

Esta compañía no venderá, cederá ni distribuirá la información personal que es recopilada sin su consentimiento, salvo que sea requerido por un juez con un orden judicial.</p>

<p>Acme Newspaper Se reserva el derecho de cambiar los términos y condiciones en cualquier momento.</p>


 </jstl:otherwise>
 </jstl:choose>


