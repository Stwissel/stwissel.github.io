$(function(){
	$('[title]').tooltip();

	$('.carousel').carousel({interval: 10000});

	$("#jumbotron").fitText(0.8, { minFontSize: '40px', maxFontSize: '70px' });

	$('#icon-size-buttons').on('click','.btn',function(){
		$('#icons i').css('font-size',jQuery(this).val());
	});
	$('#icon-shadow-buttons').on('click','.btn',function(){
		$('#icons i').css('text-shadow',jQuery(this).val());
	});
	$('#icon-color-buttons').on('click','.btn',function(){
		$('#icons i').css('color',jQuery(this).val());
	});
});
