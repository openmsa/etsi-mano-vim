package com.ubiqube.etsi.mano.vim.k8s;

import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author olivier
 *
 */
@Getter
@Setter
public class ExceptionHandler {
	private String message;
	private Throwable e;

}
