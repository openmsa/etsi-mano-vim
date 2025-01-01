/**
 * Copyright (C) 2019-2025 Ubiqube.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */
package com.ubiqube.etsi.mano.vim.k8s;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

import io.fabric8.kubernetes.api.builder.Visitor;
import io.fabric8.kubernetes.api.model.DeletionPropagation;
import io.fabric8.kubernetes.api.model.ListOptions;
import io.fabric8.kubernetes.api.model.StatusDetails;
import io.fabric8.kubernetes.api.model.autoscaling.v1.Scale;
import io.fabric8.kubernetes.client.GracePeriodConfigurable;
import io.fabric8.kubernetes.client.PropagationPolicyConfigurable;
import io.fabric8.kubernetes.client.ResourceNotFoundException;
import io.fabric8.kubernetes.client.Watch;
import io.fabric8.kubernetes.client.Watcher;
import io.fabric8.kubernetes.client.dsl.Deletable;
import io.fabric8.kubernetes.client.dsl.EditReplacePatchable;
import io.fabric8.kubernetes.client.dsl.Gettable;
import io.fabric8.kubernetes.client.dsl.Informable;
import io.fabric8.kubernetes.client.dsl.NamespaceableResource;
import io.fabric8.kubernetes.client.dsl.NonDeletingOperation;
import io.fabric8.kubernetes.client.dsl.ReplaceDeletable;
import io.fabric8.kubernetes.client.dsl.Resource;
import io.fabric8.kubernetes.client.dsl.ServerSideApplicable;
import io.fabric8.kubernetes.client.dsl.Watchable;
import io.fabric8.kubernetes.client.dsl.WritableOperation;
import io.fabric8.kubernetes.client.dsl.base.PatchContext;
import io.fabric8.kubernetes.client.informers.ResourceEventHandler;
import io.fabric8.kubernetes.client.informers.SharedIndexInformer;

public class NamespaceableResourceTest<T> implements NamespaceableResource<T> {

	private final T arg;

	public NamespaceableResourceTest(final T argument) {
		this.arg = argument;
	}

	@Override
	public boolean isReady() {
		return false;
	}

	@Override
	public T require() throws ResourceNotFoundException {
		return null;
	}

	@Override
	public ReplaceDeletable<T> lockResourceVersion() {
		return null;
	}

	@Override
	public ReplaceDeletable<T> lockResourceVersion(final String resourceVersion) {
		return null;
	}

	@Override
	public T item() {
		return null;
	}

	@Override
	public T scale(final int count) {
		return null;
	}

	@Override
	public T scale(final int count, final boolean wait) {
		return null;
	}

	@Override
	public Scale scale(final Scale scale) {
		return null;
	}

	@Override
	public Gettable<T> fromServer() {
		return null;
	}

	@Override
	public T get() {
		return null;
	}

	@Override
	public Watchable<T> withResourceVersion(final String resourceVersion) {
		return null;
	}

	@Override
	public Watch watch(final Watcher<T> watcher) {
		return null;
	}

	@Override
	public Watch watch(final ListOptions options, final Watcher<T> watcher) {
		return null;
	}

	@Override
	public Watch watch(final String resourceVersion, final Watcher<T> watcher) {
		return null;
	}

	@Override
	public T waitUntilReady(final long amount, final TimeUnit timeUnit) {
		return null;
	}

	@Override
	public T waitUntilCondition(final Predicate<T> condition, final long amount, final TimeUnit timeUnit) {
		return null;
	}

	@Override
	public T replaceStatus() {
		return null;
	}

	@Override
	public T updateStatus() {
		return null;
	}

	@Override
	public T replace() {
		return null;
	}

	@Override
	public T update() {
		return null;
	}

	@Override
	public List<StatusDetails> delete() {
		return null;
	}

	@Override
	public Deletable withTimeout(final long timeout, final TimeUnit unit) {
		return null;
	}

	@Override
	public Deletable withTimeoutInMillis(final long timeoutInMillis) {
		return null;
	}

	@Override
	public T replaceStatus(final T item) {
		return null;
	}

	@Override
	public T replace(final T item) {
		return null;
	}

	@Override
	public T createOr(final Function<NonDeletingOperation<T>, T> conflictAction) {
		return arg;
	}

	@Override
	public NonDeletingOperation<T> unlock() {
		return null;
	}

	@Override
	public T editStatus(final UnaryOperator<T> function) {
		return null;
	}

	@Override
	public T patchStatus() {
		return null;
	}

	@Override
	public EditReplacePatchable<T> subresource(final String subresource) {
		return null;
	}

	@Override
	public T createOrReplace() {
		return null;
	}

	@Override
	public T create() {
		return arg;
	}

	@Override
	public T edit(final UnaryOperator<T> function) {
		return null;
	}

	@Override
	public T edit(final Visitor... visitors) {
		return null;
	}

	@Override
	public <V> T edit(final Class<V> visitorType, final Visitor<V> visitor) {
		return null;
	}

	@Override
	public T accept(final Consumer<T> function) {
		return null;
	}

	@Override
	public T patch(final PatchContext patchContext, final T item) {
		return null;
	}

	@Override
	public T patch(final PatchContext patchContext, final String patch) {
		return null;
	}

	@Override
	public T patch() {
		return null;
	}

	@Override
	public T patch(final PatchContext patchContext) {
		return null;
	}

	@Override
	public T createOrReplace(final T item) {
		return null;
	}

	@Override
	public T create(final T item) {
		return null;
	}

	@Override
	public List<StatusDetails> delete(final T item) {
		return null;
	}

	@Override
	public T updateStatus(final T item) {
		return null;
	}

	@Override
	public T patchStatus(final T item) {
		return null;
	}

	@Override
	public PropagationPolicyConfigurable<? extends Deletable> withGracePeriod(final long gracePeriodSeconds) {
		return null;
	}

	@Override
	public GracePeriodConfigurable<? extends Deletable> withPropagationPolicy(final DeletionPropagation propagationPolicy) {
		return null;
	}

	@Override
	public T serverSideApply() {
		return null;
	}

	@Override
	public ServerSideApplicable<T> fieldManager(final String manager) {
		return null;
	}

	@Override
	public ServerSideApplicable<T> forceConflicts() {
		return null;
	}

	@Override
	public NonDeletingOperation<T> fieldValidation(final Validation fieldValidation) {
		return null;
	}

	@Override
	public WritableOperation<T> dryRun() {
		return null;
	}

	@Override
	public WritableOperation<T> dryRun(final boolean isDryRun) {
		return null;
	}

	@Override
	public Informable<T> withIndexers(final Map<String, Function<T, List<String>>> indexers) {
		return null;
	}

	@Override
	public Informable<T> withLimit(final Long limit) {
		return null;
	}

	@Override
	public SharedIndexInformer<T> inform(final ResourceEventHandler<? super T> handler, final long resync) {
		return null;
	}

	@Override
	public SharedIndexInformer<T> runnableInformer(final long resync) {
		return null;
	}

	@Override
	public CompletableFuture<List<T>> informOnCondition(final Predicate<List<T>> condition) {
		return null;
	}

	@Override
	public Resource<T> inNamespace(final String name) {
		return null;
	}

}
